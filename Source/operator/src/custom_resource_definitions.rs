use serde::{Serialize, Deserialize};

/// Represents an instance of the CRD that is in the kubernetes cluster.
/// Note that this struct does not need to implement Serialize because the
/// operator will only ever update the `status` subresource
#[derive(Deserialize, Clone, Debug, PartialEq)]
pub struct KafkaCluster {
    pub metadata: Metadata,
    pub spec: KafkaClusterSpec,
    pub status: Option<KafkaClusterStatus>,
}

/// defines only the fields we care about from the metadata. We could also just use the `ObjectMeta` struct from the `k8s_openapi` crate.
#[derive(Deserialize, Clone, Debug, PartialEq)]
pub struct Metadata {
    pub namespace: String,
    pub name: String,
}

/// The spec of our CRD
#[derive(Serialize, Deserialize, Clone, Debug, PartialEq)]
#[serde(rename_all = "camelCase")]
pub struct KafkaClusterSpec {
    pub zk_service_name: String,
    pub broker_count: u16
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq)]
#[serde(rename_all = "camelCase")]
pub struct KafkaClusterStatus {
    pub status: String,
    pub zookeeper_status: String,
    pub broker_count: String,
    pub broker_status: String
}
