#!/bin/bash
shopt -s nullglob
for f in *.jar
do
	for pod in $(kubectl --namespace sc get pods -l "app=spark-worker" --output=jsonpath={.items..metadata.name}); do
		echo "Uploading file $f to pod $pod"
		kubectl --namespace sc exec $pod -- rm $f
		kubectl --namespace sc cp "$f" /"$pod":/"$f"
		echo "Finished uploading file $f to pod $pod"
		sleep 5s
	done
	for pod in $(kubectl --namespace sc get pods -l "app=spark-master" --output=jsonpath={.items..metadata.name}); do
		echo "Uploading file $f to pod $pod"
		kubectl --namespace sc exec $pod -- rm $f
		kubectl --namespace sc cp "$f" /"$pod":/"$f"
		echo "Finished uploading file $f to pod $pod"
		sleep 5s
	done
done