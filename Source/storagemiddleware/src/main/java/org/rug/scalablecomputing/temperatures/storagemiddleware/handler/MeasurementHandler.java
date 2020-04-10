package org.rug.scalablecomputing.temperatures.storagemiddleware.handler;

import org.rug.scalablecomputing.temperatures.storagemiddleware.exporter.KafkaExporter;
import org.rug.scalablecomputing.temperatures.storagemiddleware.model.BatchJobRequest;
import org.rug.scalablecomputing.temperatures.storagemiddleware.model.Measurement;
import org.rug.scalablecomputing.temperatures.storagemiddleware.repositories.MeasurementsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class MeasurementHandler {

    @Autowired
    private KafkaExporter kafkaExporter;

    @Autowired
    private MeasurementsRepository measurementsRepository;

    public void handle(Measurement measurement) {
        System.out.println(measurement);
        saveMeasurement(measurement);
        createAndSaveBatchJobs(measurement);
    }

    private void createAndSaveBatchJobs(Measurement measurement) {
        BatchJobRequest request = null;
        // If last hour of the day, create daily batch job
        if(measurement.getHour() == 24) {
            createAndSaveDailyJob(measurement);

            if(lastDayOfWeek(measurement.getDate())) {
                createAndSaveWeeklyJob(measurement);
            }

            if(lastDayOfMonth(measurement.getDate())) {
                createAndSaveMonthlyJob(measurement);
            }
        }
    }

    private void createAndSaveMonthlyJob(Measurement measurement) {
        LocalDate date = measurement.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        BatchJobRequest request = new BatchJobRequest(
                measurement.getStation(),
                date.minusDays(date.getDayOfMonth()-1),
                date
        );
        kafkaExporter.export(request, "monthly");
    }

    private boolean lastDayOfMonth(Date date) {
        LocalDate dateLocal = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return dateLocal.getMonth() != dateLocal.plusDays(1).getMonth();
    }

    private void createAndSaveWeeklyJob(Measurement measurement) {
        LocalDate dateLocal = measurement.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        BatchJobRequest request = new BatchJobRequest(
                measurement.getStation(),
                dateLocal.minusDays(6),
                dateLocal
        );
        kafkaExporter.export(request, "weekly");
    }

    private boolean lastDayOfWeek(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private void createAndSaveDailyJob(Measurement measurement) {
        LocalDate dateLocal = measurement.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        BatchJobRequest request = new BatchJobRequest(
                measurement.getStation(),
                dateLocal,
                dateLocal
        );
        kafkaExporter.export(request, "daily");
    }

    private void saveMeasurement(Measurement measurement) {
        measurementsRepository.save(measurement).subscribe();
    }
}
