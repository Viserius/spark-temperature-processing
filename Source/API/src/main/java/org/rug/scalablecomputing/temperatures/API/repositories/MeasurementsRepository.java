package org.rug.scalablecomputing.temperatures.API.repositories;

import org.rug.scalablecomputing.temperatures.API.models.Measurement;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;

public interface MeasurementsRepository extends ReactiveCrudRepository<Measurement, Integer> {

    @AllowFiltering
    Flux<Measurement> findByStationAndDateGreaterThanEqual(final int station, final Instant date);

    @AllowFiltering
    Flux<Measurement> findByStationAndDateGreaterThanEqualAndDateLessThanEqual(final int station,
                                                                               final Instant fromDate,
                                                                               final Instant toDate);

}