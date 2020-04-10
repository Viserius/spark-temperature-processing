package org.rug.scalablecomputing.temperatures.API.repositories.batchaverages;

import org.rug.scalablecomputing.temperatures.API.models.averages.MonthlyAverage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;

public interface MonthlyAveragesRepository extends ReactiveCrudRepository<MonthlyAverage, Integer> {

    Flux<MonthlyAverage> findByStation(final int station);

    Flux<MonthlyAverage> findByStationAndDateGreaterThanEqual(final int station, final Instant fromDate);

    Flux<MonthlyAverage> findByStationAndDateGreaterThanEqualAndDateLessThanEqual(final int station,
                                                                                  final Instant fromDate,
                                                                                  final Instant toDate);

}