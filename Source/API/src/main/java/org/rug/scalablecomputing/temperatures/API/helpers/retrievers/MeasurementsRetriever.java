package org.rug.scalablecomputing.temperatures.API.helpers.retrievers;

import org.rug.scalablecomputing.temperatures.API.helpers.Conversions;
import org.rug.scalablecomputing.temperatures.API.models.Measurement;
import org.rug.scalablecomputing.temperatures.API.models.averages.DailyAverage;
import org.rug.scalablecomputing.temperatures.API.repositories.MeasurementsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class MeasurementsRetriever {

    @Autowired
    private MeasurementsRepository measurementsRepository;

    public Mono<List<Measurement>> getMeasurements(@PathVariable int station,
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                   @RequestParam(name = "startDate", required = false)
                                                   LocalDate fromDate,
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                   @RequestParam(name = "endDate", required = false)
                                                   LocalDate toDate, Mono<List<DailyAverage>> dailyAverageFlux) {
        Mono<List<Measurement>> measurementFlux;
        if(toDate == null || fromDate == null) {
            // If there is no upper bound, don't show all measurements, but only those after the averages
            measurementFlux = dailyAverageFlux.flatMap(dailyAverageList -> {
                        if(dailyAverageList.size() > 0)
                            return this.measurementsRepository.findByStationAndDateGreaterThanEqual(station,
                                dailyAverageList.get(dailyAverageList.size() - 1).getDate()
                            ).collectList();
                        else
                            return Mono.just(new ArrayList<Measurement>());
                    }
            );
        } else {
            // If there IS an upper bound, show only measurements in range
            measurementFlux = this.measurementsRepository.findByStationAndDateGreaterThanEqualAndDateLessThanEqual(
                    station,
                    Conversions.LocalDateToInstant(fromDate),
                    Conversions.LocalDateToInstant(toDate)
            ).collectList();
        }
        return measurementFlux;
    }

}
