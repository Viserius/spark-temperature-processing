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

kubectl ${action} -f cassandra-service.yaml
kubectl ${action} -f cassandra-statefulset.yaml

if [[ ${action} = apply ]]; then
	echo "Waiting for cassandra to become ready..."
	kubectl wait pod --for condition=ready -l app=cassandra --timeout=300s --namespace=sc
	sleep 10s
fi