package org.rug.scalablecomputing.temperatures.simulator.faker;

import org.rug.scalablecomputing.temperatures.simulator.domain.Measurement;
import org.rug.scalablecomputing.temperatures.simulator.exporter.KafkaExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

@Component
@Scope("prototype")
public class FakerRunner implements Runnable {

    @Autowired
    private KafkaExporter kafkaExporter;

    private Random random;
    private int stationId;
    private LocalDate currentDate;
    private int currentHour;
    private int currentTemperature;

    public FakerRunner()
    {
        this.random = new Random();
        this.stationId = random.nextInt(9000) + 1000; // random station id between 1000 and 10000
        this.currentDate = LocalDate.of(1900, 1, 1);
        this.currentHour = 1;
        this.currentTemperature = 0;
    }

    @Override
    public void run() {
        while(true) {
            exportMeasurement();
            passTime();
        }
    }

    private void passTime() {
        if(this.currentHour == 24) {
            this.currentHour = 1;
            this.currentDate = this.currentDate.plusDays(1);
        } else {
            this.currentHour++;
        }
        this.currentTemperature += this.random.nextInt(5) - 2; // add or subtract 1 or 2 degrees
    }

    private void exportMeasurement() {
        Measurement measurement = new Measurement(
                this.stationId,
                this.currentDate,
                this.currentHour,
                this.currentTemperature
        );
        this.kafkaExporter.export(measurement);
    }

}
