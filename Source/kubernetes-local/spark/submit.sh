kubectl exec -it --namespace="sc" $(kubectl get pod -l "app=spark-master" -o jsonpath='{.items[0].metadata.name}' --namespace=sc) -- bash ./spark/bin/spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.5 --class org.scalablecomputing.spark.Main --master spark://spark-master:7077 --deploy-mode client --conf spark.driver.host=spark-master --num-executors 1 --executor-cores 1 /spark-streaming-averages.jar 

#&
#kubectl exec -it --namespace="sc" $(kubectl get pod -l "app=spark-master" -o jsonpath='{.items[0].metadata.name}' --namespace=sc) -- bash ./spark/bin/spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.5 --class org.scalablecomputing.spark.Main --master spark://spark-master:7077 --deploy-mode client --conf spark.driver.host=spark-master /spark-batch-averages.jar daily &
#kubectl exec -it --namespace="sc" $(kubectl get pod -l "app=spark-master" -o jsonpath='{.items[0].metadata.name}' --namespace=sc) -- bash ./spark/bin/spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.5 --class org.scalablecomputing.spark.Main --master spark://spark-master:7077 --deploy-mode client --conf spark.driver.host=spark-master /spark-batch-averages.jar weekly &
#kubectl exec -it --namespace="sc" $(kubectl get pod -l "app=spark-master" -o jsonpath='{.items[0].metadata.name}' --namespace=sc) -- bash ./spark/bin/spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.5 --class org.scalablecomputing.spark.Main --master spark://spark-master:7077 --deploy-mode client --conf spark.driver.host=spark-master /spark-batch-averages.jar monthly &