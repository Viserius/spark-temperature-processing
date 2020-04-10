package org.rug.scalablecomputing.temperatures.API.repositories;

import org.rug.scalablecomputing.temperatures.API.models.Measurement;
import org.rug.scalablecomputing.temperatures.API.models.StationAverage;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface AveragesRepository extends ReactiveCrudRepository<StationAverage, Integer> {

    Mono<StationAverage> findFirstByStation(final int station);

}