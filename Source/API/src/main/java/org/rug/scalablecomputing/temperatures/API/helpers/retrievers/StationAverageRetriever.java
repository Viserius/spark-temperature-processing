package org.rug.scalablecomputing.temperatures.API.helpers.retrievers;

import org.rug.scalablecomputing.temperatures.API.models.StationAverage;
import org.rug.scalablecomputing.temperatures.API.repositories.AveragesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class StationAverageRetriever {

    @Autowired
    private AveragesRepository averagesRepository;

    public Mono<StationAverage> getStationAverages(int station) {
        return this.averagesRepository.findFirstByStation(station);
    }

    public Flux<StationAverage> getAll() {
        return this.averagesRepository.findAll();
    }
}
