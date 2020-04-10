package org.rug.scalablecomputing.temperatures.storagemiddleware.importer;

import org.rug.scalablecomputing.temperatures.storagemiddleware.StoragemiddlewareApplication;
import org.rug.scalablecomputing.temperatures.storagemiddleware.handler.MeasurementHandler;
import org.rug.scalablecomputing.temperatures.storagemiddleware.model.Measurement;
import org.rug.scalablecomputing.temperatures.storagemiddleware.repositories.MeasurementsRepository;
import org.rug.scalablecomputing.temperatures.storagemiddleware.streams.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class KafkaImporter {

    @Autowired
    public MeasurementHandler measurementHandler;

    @StreamListener(Streams.INPUT)
    public void importer(Measurement measurement)
    {
        measurementHandler.handle(measurement);
    }
}
