package org.rug.scalablecomputing.temperatures.API.repositories;

import org.rug.scalablecomputing.temperatures.API.models.Measurement;
import org.rug.scalablecomputing.temperatures.API.models.Prediction;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;

public interface PredictionsRepository extends ReactiveCrudRepository<Prediction, Integer> {

    @AllowFiltering
    Flux<Prediction> findByStationAndDateGreaterThanEqual(final int station, final Instant date);

    @AllowFiltering
    Flux<Prediction> findByStationAndDateGreaterThanEqualAndDateLessThanEqual(final int station,
                                                                              final Instant fromDate,
                                                                              final Instant toDate);

}