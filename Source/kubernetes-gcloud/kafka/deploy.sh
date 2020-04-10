#!/usr/bin/env bash
# Change directory to the one of this script.
cd "${0%/*}"

# Check arg count
if [[ $# > 1 ]]; then
    echo "Illegal number of arguments."
    exit
fi

# if no args, action=${action}
if [[ $# = 0 ]]; then
    action=apply
# Else we check if ${action} or delete or illegal
elif [[ $1 = "apply" ]]; then
            action=apply
        elif [[ $1 = "delete" ]]; then
            action=delete
        else
            echo "Illegal argument passed."
            exit
fi

kubectl ${action} -f zookeeper.yaml
kubectl ${action} -f zookeeper-service.yaml

if [[ ${action} = apply ]]; then
	echo "Waiting for zookeeper to become ready..."
	kubectl wait pod --for condition=ready -l component=zookeeper --timeout=300s
	sleep 10s
fi

kubectl ${action} -f brokers.yaml
kubectl ${action} -f kafka-service.yaml

if [[ ${action} = apply ]]; then
	echo "Waiting for kafka-1 to become ready..."
	kubectl wait pod --for condition=ready -l component=kafka-1 --timeout=300s
	sleep 10s

	echo "Waiting for kafka-2 to become ready..."
	kubectl wait pod --for condition=ready -l component=kafka-2 --timeout=300s
	sleep 10s

	echo "Waiting for kafka-3 to become ready..."
	kubectl wait pod --for condition=ready -l component=kafka-3 --timeout=300s
	sleep 10s

	echo "Creating kafka topic..."
	./create-topic.sh
fi
kubectl ${action} -f kafdrop.yaml


# kafka-topics.sh --create --zookeeper zookeeper-service:2181 --replication-factor 3 --partitions 3 --topic temperatures_in

# test with kafka kat: run kubectl port-forward {kafka-1-podname} 9092:9092
# second terminal: kafkacat -L -b localhost:9092