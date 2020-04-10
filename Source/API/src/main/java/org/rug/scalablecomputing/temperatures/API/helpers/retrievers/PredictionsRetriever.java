package org.rug.scalablecomputing.temperatures.API.helpers.retrievers;

import org.rug.scalablecomputing.temperatures.API.helpers.Conversions;
import org.rug.scalablecomputing.temperatures.API.models.Measurement;
import org.rug.scalablecomputing.temperatures.API.models.Prediction;
import org.rug.scalablecomputing.temperatures.API.models.averages.DailyAverage;
import org.rug.scalablecomputing.temperatures.API.repositories.MeasurementsRepository;
import org.rug.scalablecomputing.temperatures.API.repositories.PredictionsRepository;
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
public class PredictionsRetriever {

    @Autowired
    private PredictionsRepository predictionsRepository;

    public Mono<List<Prediction>> getPredictions(@PathVariable int station,
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                   @RequestParam(name = "startDate", required = false)
                                                   LocalDate fromDate,
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                   @RequestParam(name = "endDate", required = false)
                                                   LocalDate toDate, Mono<List<Measurement>> measurementFlux) {
        Mono<List<Prediction>> predictionsFlux;
        if(toDate == null || fromDate == null) {
            // If there is no upper bound, don't show all measurements, but only those after the averages
            predictionsFlux = measurementFlux.flatMap(measurementList -> {
                if(measurementList.size() > 0)
                    return this.predictionsRepository.findByStationAndDateGreaterThanEqual(station,
                            measurementList.get(measurementList.size() - 1).getDate()
                    ).collectList();
                else
                    return Mono.just(new ArrayList<Prediction>());
            });
        } else {
            // If there IS an upper bound, show all measurements in range
            predictionsFlux = this.predictionsRepository.findByStationAndDateGreaterThanEqualAndDateLessThanEqual(
                    station,
                    Conversions.LocalDateToInstant(fromDate),
                    Conversions.LocalDateToInstant(toDate)
            ).collectList();
        }
        return predictionsFlux;
    }

}
