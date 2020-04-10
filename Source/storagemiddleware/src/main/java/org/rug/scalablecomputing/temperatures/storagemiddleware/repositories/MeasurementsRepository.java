package org.rug.scalablecomputing.temperatures.storagemiddleware.repositories;

import org.rug.scalablecomputing.temperatures.storagemiddleware.model.Measurement;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MeasurementsRepository extends ReactiveCrudRepository<Measurement, Integer> {
}