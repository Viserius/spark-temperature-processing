package org.rug.scalablecomputing.temperatures.API.repositories.batchaverages;

import org.rug.scalablecomputing.temperatures.API.models.averages.DailyAverage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;

public interface DailyAveragesRepository extends ReactiveCrudRepository<DailyAverage, Integer> {

    Flux<DailyAverage> findByStation(final int station);

    Flux<DailyAverage> findByStationAndDateGreaterThanEqual(final int station, final Instant fromDate);

    Flux<DailyAverage> findByStationAndDateGreaterThanEqualAndDateLessThanEqual(final int station,
                                                                                final Instant fromDate,
                                                                                final Instant toDate);

}