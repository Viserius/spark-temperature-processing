package org.rug.scalablecomputing.temperatures.simulator.exporter;

import org.apache.kafka.clients.admin.NewTopic;
import org.rug.scalablecomputing.temperatures.simulator.domain.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaExporter {

    private KafkaTemplate<Integer, Object> template;
    @Value("${partitions}")
    private int topicPartitions;
    @Value("${replicas}")
    private int topicReplicas;
    @Value("${fixed_delay_ms:0}")
    private int fixedDelayInMs;

    @Autowired
    public KafkaExporter(KafkaTemplate<Integer, Object> template) {
        this.template = template;
    }

    public void export(Measurement measurement)
    {
        System.out.println("Printing Measurement: " + measurement);
        this.template.send("temperatures-in", measurement.getStation(), measurement);
        try {
            Thread.sleep(fixedDelayInMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("temperatures-in")
                .partitions(this.topicPartitions)
                .replicas(this.topicReplicas)
                .compact()
                .build();
    }
}
