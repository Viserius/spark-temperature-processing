package org.rug.scalablecomputing.temperatures.simulator.parser;

import org.rug.scalablecomputing.temperatures.simulator.domain.Measurement;
import org.rug.scalablecomputing.temperatures.simulator.exporter.KafkaExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Component
public class ParserRunner implements Runnable {

    private FileHandler fileHandler;
    private DataParser dataParser;

    @Value("${filename}")
    private String fileNameData;

    @Autowired
    private KafkaExporter kafkaExporter;

    public ParserRunner() {
        this.fileHandler = new FileHandler();
        this.dataParser = new DataParser();
    }

    public void run() {
        // Request access to file from OS
        File dataFile = fileHandler.getFile(this.fileNameData);
        Scanner dataScanner = fileHandler.getScanner(dataFile);

        // Process each line
        while (dataScanner.hasNextLine()) {
            Measurement measurement = dataParser.parseLine(dataScanner.nextLine());
            if(measurement == null) continue;

            kafkaExporter.export(measurement);
        }
        dataScanner.close();

    }

}
