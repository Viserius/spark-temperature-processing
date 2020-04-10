package org.rug.scalablecomputing.temperatures.API.repositories.batchaverages;

import org.rug.scalablecomputing.temperatures.API.models.averages.WeeklyAverage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;

public interface WeeklyAveragesRepository extends ReactiveCrudRepository<WeeklyAverage, Integer> {

    Flux<WeeklyAverage> findByStation(final int station);

    Flux<WeeklyAverage> findByStationAndDateGreaterThanEqual(final int station, final Instant fromDate);

    Flux<WeeklyAverage> findByStationAndDateGreaterThanEqualAndDateLessThanEqual(final int station,
                                                                                 final Instant fromDate,
                                                                                 final Instant toDate);

}