#[macro_use]
extern crate log;

mod pod_templates;
pub use pod_templates::*;

mod custom_resource_definitions;
pub use custom_resource_definitions::*;

use serde::{Serialize, Deserialize};
use k8s_openapi::*;
use roperator::config::{ClientConfig, Credentials, KubeConfig};
use roperator::prelude::{
    k8s_types, ChildConfig, Error, K8sType, OperatorConfig, SyncRequest, SyncResponse,
};
use roperator::serde_json::{json, Value};
use k8s_openapi::api::core::v1::Pod;
use std::sync::mpsc::SyncSender;

const OPERATOR_NAME: &str = "scalable-operator";
static PARENT_TYPE: &K8sType = &K8sType {
    api_version: "scalablecomputing.roperator.com/v1alpha1",
    kind: "KafkaCluster",
    plural_kind: "kafkaclusters",
};

fn main() {

    if std::env::var("RUST_LOG").is_err() {
        std::env::set_var("RUST_LOG", "roperator=info,warn");
    }
    env_logger::init();

    let operator_config = OperatorConfig::new(OPERATOR_NAME, PARENT_TYPE)
        .with_child(k8s_types::core::v1::Pod, ChildConfig::recreate())
        .with_child(k8s_types::core::v1::Service, ChildConfig::replace());


    let client_config_result = if let Ok(token) = std::env::var("ROPERATOR_AUTH_TOKEN") {
        let credentials = Credentials::base64_bearer_token(token);
        let (kubeconfig, kubeconfig_path) = KubeConfig::load().expect("failed to load kubeconfig");
        let kubeconfig_parent_path = kubeconfig_path.parent().unwrap();
        kubeconfig.create_client_config_with_credentials(
            OPERATOR_NAME.to_string(),
            kubeconfig_parent_path,
            credentials,
        )
    } else {
        ClientConfig::from_kubeconfig(OPERATOR_NAME.to_string())
    };
    let client_config =
        client_config_result.expect("failed to resolve cluster data from kubeconfig");

    println!("{:?}", client_config);

    // now we run the operator, passing in our handler function
    let err = roperator::runner::run_operator_with_client_config(
        operator_config,
        client_config,
        handle_sync,
    );


    // `run_operator_with_client_config` will never return under normal circumstances, so we only need to handle the sad path here
    log::error!("Error running operator: {}", err);
    std::process::exit(1);

}

fn handle_sync(request: &SyncRequest) -> Result<SyncResponse, Error> {
    log::info!("Got sync request: {:?}", request);

    let custom_resource: KafkaCluster = request.deserialize_parent()?;
    let mut status = get_current_status_message(request, &custom_resource);
    let children = get_desired_children(request, &mut status, &custom_resource)?;
    let mut response = SyncResponse::from_status(status).unwrap();
    response.children = children;
    Ok(response)
}

fn get_current_status_message(request: &SyncRequest, crd: &KafkaCluster) -> KafkaClusterStatus {
    let mut kafka_status = KafkaClusterStatus {
        status: "Unknown".to_string(),
        zookeeper_status: "Unknown".to_string(),
        broker_count: "Unknown".to_string(),
        broker_status: "Unknown".to_string()
    };

    let mut zk_ready= "Unknown".to_string();
    let mut ready_nodes = 0;
    let mut total_nodes = crd.spec.broker_count;

    for pod in request.children().of_type::<Pod>("v1", "Pod").iter() {
        let pod = match pod { Ok(v) => v, Err(_) => continue };
        if let Some(metadata) = pod.metadata {
            if let Some(labels) = metadata.labels {
                if let Some(st) = pod.status {
                    if let Some(label) = labels.get(&"component".to_string()) {
                        if label == "zookeeper" {
                            if let Some(v) = st.phase {
                                zk_ready = v;
                                continue;
                            }
                        }
                    }
                    if let Some(label) = labels.get(&"broker".to_string()) {
                        if label == "kafka" {
                            if let Some(t) = st.conditions {
                                for c in &t {
                                    if c.type_ == "Ready" && c.status == "True" {
                                        ready_nodes += 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    kafka_status.zookeeper_status = zk_ready;
    kafka_status.broker_status = format!("{}/{}", ready_nodes, total_nodes).to_string();

    if kafka_status.zookeeper_status == "Running".to_owned() && ready_nodes == total_nodes {
        kafka_status.status = "Healthy".to_owned();
    } else {
        kafka_status.status = "Unhealthy".to_owned();
    }

    return kafka_status;
}

/// Returns the children that we want for the given parent
fn get_desired_children(request: &SyncRequest, status: &mut KafkaClusterStatus, crd: &KafkaCluster) -> Result<Vec<Value>, Error> {
    let custom_resource: KafkaCluster = request.deserialize_parent()?;
    let mut items: Vec<Value> = vec![];

    items.push(get_zookeeper_node(&custom_resource.metadata.namespace, &crd.spec.zk_service_name, 2181));
    items.push(get_zookeeper_service(&custom_resource.metadata.namespace, &crd.spec.zk_service_name, 2181));

    if status.zookeeper_status == "Running" {
        items.push(get_headless_kafka_service(&custom_resource.metadata.namespace, &"kafka".to_owned(), 9092));
        for i in 0..custom_resource.spec.broker_count {
            let broker = KafkaNodeInfo {
                namespace: custom_resource.metadata.namespace.clone(),
                node_id: i as u32,
                zookeeper_address: crd.spec.zk_service_name.clone(),
                zookeeper_port: 2181
            };
            items.push(get_kafka_service(&custom_resource.metadata.namespace, &"kafka".to_owned(), 9092, broker.node_id));
            items.push(get_kafka_node(&broker));
        }
    }

    Ok(items)
}