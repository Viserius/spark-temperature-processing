use serde::{Serialize, Deserialize};
use roperator::serde_json::{json, *};
use k8s_openapi::*;
use k8s_openapi::api::core::v1::Pod;
use log::MetadataBuilder;
use k8s_openapi::apimachinery::pkg::apis::meta::v1::ObjectMeta;

pub struct KafkaNodeInfo {
    pub namespace: String,
    pub node_id: u32,
    pub zookeeper_address: String,
    pub zookeeper_port: u32
}

pub fn get_zookeeper_service(namespace: &String, address: &String, port: u32) -> Value {
    let service = json!({
        "apiVersion": "v1",
        "kind": "Service",
        "metadata": {
            "namespace": namespace,
            "name": address,
        },
        "spec": {
            "type": "ClusterIP",
            "selector": {
                "component": "zookeeper",
            },
            "ports": [
                {
                    "name": "zookeeper",
                    "port": port,
                    "targetPort": port,
                }
            ]
        }
    });
    service
}

pub fn get_zookeeper_node(namespace: &String, address: &String, port: u32) -> Value {
    let pod = json!({
        "apiVersion": "v1",
        "kind": "Pod",
        "metadata": {
            "namespace": namespace,
            "name": "zookeeper",
            "labels": {
                "component": "zookeeper",
            }
        },
        "spec": {
            "containers": [
                {
                    "name": "zookeeper",
                    "image": "digitalwonderland/zookeeper",
                    "env": [
                        {
                            "name": "ZOOKEEPER_ID",
                            "value": "1",
                        },
                        {
                            "name": "ZOOKEEPER_SERVER_1",
                            "value": address
                        }
                    ],
                    "ports": [
                        { "containerPort": port }
                    ],
                    "resources": {
                        "requests": {
                            "cpu": "125m",
                            "memory": "64Mi"
                        },
                        "limits": {
                            "cpu": "250m",
                            "memory": "128Mi"
                        }
                    }
                }
            ]
        }
    });
    return pod;
}

pub fn get_kafka_node(info: &KafkaNodeInfo) -> Value {
    let pod = json! ({
        "apiVersion": "v1",
        "kind": "Pod",
        "metadata": {
            "namespace": info.namespace,
            "name": &format!("kafka-{}", info.node_id).to_string(),
            "labels": {
                "broker": "kafka",
                "component": &format!("kafka-{}", info.node_id).to_string(),
            }
        },
        "spec": {
            "containers": [
                {
                    "name": &format!("kafka-{}", info.node_id).to_string(),
                    "image": "pharosproduction/kafka_k8s:v1",
                    "resources": {
                        "requests": {
                            "memory": "128Mi",
                            "cpu": "125m"
                        },
                        "limits": {
                            "memory": "512Mi",
                            "cpu": "500m"
                        },
                    },
                    "ports": [
                        { "containerPort": 9092 }
                    ],
                    "env": [
                        {
                            "name": "MY_POD_IP",
                            "valueFrom": {
                                "fieldRef": {
                                    "fieldPath": "status.podIP"
                                }
                            },
                        },
                        {
                            "name": "KAFKA_ADVERTISED_PORT",
                            "value": "9092",
                        },
                        {
                            "name": "KAFKA_ZOOKEEPER_CONNECT",
                            "value": &format!("{}:{}", info.zookeeper_address, info.zookeeper_port),
                        },
                        {
                            "name": "KAFKA_ADVERTISED_HOST_NAME",
                            "value": "$(MY_POD_IP)",
                        },
                        {
                            "name": "KAFKA_AUTO_CREATE_TOPICS_ENABLE",
                            "value": "false",
                        },
                        {
                            "name": "KAFKA_BROKER_ID",
                            "value": &format!("{}", info.node_id).to_string(),
                        },
                    ],
                    "tty": true,
                    "livenessProbe": {
                      "exec": {
                        "command": [
                            "/opt/check.sh"
                      ]},
                      "initialDelaySeconds": 30,
                      "periodSeconds": 30,
                    },
                    "readinessProbe": {
                      "exec": {
                        "command": [
                            "/opt/check.sh"
                      ]},
                      "initialDelaySeconds": 30,
                      "periodSeconds": 5,
                    }
                }
            ]
        }
    });


    return serde_json::to_value(pod).unwrap()
}

pub fn get_headless_kafka_service(namespace: &String, address: &String, port: u32) -> Value {
    let service = json!({
        "apiVersion": "v1",
        "kind": "Service",
        "metadata": {
            "namespace": namespace,
            "name": "kafka-service",
        },
        "spec": {
            "type": "ClusterIP",
            "selector": {
                "broker": "kafka",
            },
            "ports": [
                {
                    "port": port,
                    "targetPort": port,
                }
            ]
        }
    });
    service
}

pub fn get_kafka_service(namespace: &String, address: &String, port: u32, kafka_id: u32) -> Value {
    let service = json!({
        "apiVersion": "v1",
        "kind": "Service",
        "metadata": {
            "namespace": namespace,
            "name": &format!("{}-{}-service", address, kafka_id),
        },
        "spec": {
            "type": "ClusterIP",
            "selector": {
                "component": &format!("kafka-{}", kafka_id),
            },
            "ports": [
                {
                    "name": "kafka",
                    "port": port,
                    "targetPort": port,
                }
            ]
        }
    });
    service
}