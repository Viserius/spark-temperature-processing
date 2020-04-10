package org.rug.scalablecomputing.temperatures.storagemiddleware.streams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

public interface Streams {
    @Value("${kafka_input_topic}")
    String INPUT = "temperatures-in";

    @Input(INPUT)
    SubscribableChannel incomingTemp();
}
