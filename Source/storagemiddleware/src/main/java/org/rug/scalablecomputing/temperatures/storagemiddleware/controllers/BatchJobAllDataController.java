package org.rug.scalablecomputing.temperatures.storagemiddleware.controllers;

import com.datastax.driver.core.querybuilder.Batch;
import org.rug.scalablecomputing.temperatures.storagemiddleware.exporter.KafkaExporter;
import org.rug.scalablecomputing.temperatures.storagemiddleware.model.BatchJobRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class BatchJobAllDataController {

    @Autowired
    private KafkaExporter kafkaExporter;

    @RequestMapping(value = "/createjob", method = RequestMethod.POST)
    public BatchJobRequest createjob(@RequestBody BatchJobRequest batchJobRequest) {
        System.out.println(batchJobRequest);
        this.kafkaExporter.export(batchJobRequest, "all");
        return batchJobRequest;
    }

}