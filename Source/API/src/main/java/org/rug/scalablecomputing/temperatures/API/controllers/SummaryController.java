package org.rug.scalablecomputing.temperatures.API.controllers;

import org.rug.scalablecomputing.temperatures.API.helpers.retrievers.BatchAveragesRetriever;
import org.rug.scalablecomputing.temperatures.API.helpers.retrievers.MeasurementsRetriever;
import org.rug.scalablecomputing.temperatures.API.helpers.retrievers.PredictionsRetriever;
import org.rug.scalablecomputing.temperatures.API.helpers.retrievers.StationAverageRetriever;
import org.rug.scalablecomputing.temperatures.API.models.Measurement;
import org.rug.scalablecomputing.temperatures.API.models.Prediction;
import org.rug.scalablecomputing.temperatures.API.models.StationAverage;
import org.rug.scalablecomputing.temperatures.API.models.averages.DailyAverage;
import org.rug.scalablecomputing.temperatures.API.models.averages.MonthlyAverage;
import org.rug.scalablecomputing.temperatures.API.models.Summary;
import org.rug.scalablecomputing.temperatures.API.models.averages.WeeklyAverage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
public class SummaryController {

    @Autowired
    private BatchAveragesRetriever batchAveragesRetriever;

    @Autowired
    private MeasurementsRetriever measurementsRetriever;

    @Autowired
    private PredictionsRetriever predictionsRetriever;

    @Autowired
    private StationAverageRetriever stationAverageRetriever;

    @GetMapping("/{station}")
    public Mono<Summary> getAverages(@PathVariable int station,
                                          @RequestParam(name = "startDate", required = false)
                            @DateTimeFormat(pattern="yyyy-MM-dd")
                            LocalDate fromDate,
                                          @RequestParam(name = "endDate", required = false)
                            @DateTimeFormat(pattern="yyyy-MM-dd")
                            LocalDate toDate) {

        // Obtain all corresponding averages
        Mono<List<DailyAverage>> dailyAverageFlux = batchAveragesRetriever
                .getDailyAverages(station, fromDate, toDate);
        Mono<List<WeeklyAverage>> weeklyAverageFlux = batchAveragesRetriever
                .getWeeklyAverage(station, fromDate, toDate);
        Mono<List<MonthlyAverage>> monthlyAverageFlux = batchAveragesRetriever
                .getMonthlyAverage(station, fromDate, toDate);

        // Obtain all desired measurements
        Mono<List<Measurement>> measurementFlux = this.measurementsRetriever
                .getMeasurements(station, fromDate, toDate, dailyAverageFlux);

        // Obtain all desired predictions
        Mono<List<Prediction>> predictionMono = this.predictionsRetriever
                .getPredictions(station, fromDate, toDate, measurementFlux);

        // Obtain the realtime station average
        Mono<StationAverage> stationAverageMono = this.stationAverageRetriever
                .getStationAverages(station);

        // Return as summary object
        return Mono.zip(dailyAverageFlux,
                weeklyAverageFlux,
                monthlyAverageFlux,
                measurementFlux,
                predictionMono,
                stationAverageMono).map(x ->
                new Summary(x.getT1(), x.getT2(), x.getT3(),
                        x.getT4(), x.getT5(), x.getT6())
        );
    }
}
