#!/usr/bin/env bash

kubectl exec -i -t $(kubectl get pod -l "component=kafka-1" -o jsonpath='{.items[0].metadata.name}') -- kafka-topics.sh --create --zookeeper zookeeper-service:2181 --replication-factor 2 --partitions 3 --topic temperatures-in
kubectl exec -i -t $(kubectl get pod -l "component=kafka-1" -o jsonpath='{.items[0].metadata.name}') -- kafka-topics.sh --create --zookeeper zookeeper-service:2181 --replication-factor 2 --partitions 3 --topic batchjobs-daily
kubectl exec -i -t $(kubectl get pod -l "component=kafka-1" -o jsonpath='{.items[0].metadata.name}') -- kafka-topics.sh --create --zookeeper zookeeper-service:2181 --replication-factor 2 --partitions 3 --topic batchjobs-weekly
kubectl exec -i -t $(kubectl get pod -l "component=kafka-1" -o jsonpath='{.items[0].metadata.name}') -- kafka-topics.sh --create --zookeeper zookeeper-service:2181 --replication-factor 3 --partitions 3 --topic batchjobs-monthly
kubectl exec -i -t $(kubectl get pod -l "component=kafka-1" -o jsonpath='{.items[0].metadata.name}') -- kafka-topics.sh --create --zookeeper zookeeper-service:2181 --replication-factor 3 --partitions 3 --topic batchjobs-all