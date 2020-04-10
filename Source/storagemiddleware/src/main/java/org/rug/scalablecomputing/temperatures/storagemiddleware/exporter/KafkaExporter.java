package org.rug.scalablecomputing.temperatures.storagemiddleware.exporter;

import org.apache.kafka.clients.admin.NewTopic;
import org.rug.scalablecomputing.temperatures.storagemiddleware.model.BatchJobRequest;
import org.rug.scalablecomputing.temperatures.storagemiddleware.model.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaExporter {
    private KafkaTemplate<Object, Object> template;
    @Value("${partitions}")
    private int topicPartitions;
    @Value("${replicas}")
    private int topicReplicas;

    @Autowired
    public KafkaExporter(KafkaTemplate<Object, Object> template) {
        this.template = template;
    }

    public void export(BatchJobRequest request, String timeframe) {
        this.template.send("batchjobs-" + timeframe, request.getStation(), request);
    }

    @Bean
    public NewTopic topicDaily() {
        return TopicBuilder.name("batchjobs-daily")
                .partitions(this.topicPartitions)
                .replicas(this.topicReplicas)
                .compact()
                .build();
    }

    @Bean
    public NewTopic topicWeekly() {
        return TopicBuilder.name("batchjobs-weekly")
                .partitions(this.topicPartitions)
                .replicas(this.topicReplicas)
                .compact()
                .build();
    }

    @Bean
    public NewTopic topicMonthly() {
        return TopicBuilder.name("batchjobs-monthly")
                .partitions(this.topicPartitions)
                .replicas(this.topicReplicas)
                .compact()
                .build();
    }

    @Bean
    public NewTopic topicAll() {
        return TopicBuilder.name("batchjobs-all")
                .partitions(this.topicPartitions)
                .replicas(this.topicReplicas)
                .compact()
                .build();
    }
}
