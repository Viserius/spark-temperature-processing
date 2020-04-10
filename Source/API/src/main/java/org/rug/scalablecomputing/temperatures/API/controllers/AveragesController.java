package org.rug.scalablecomputing.temperatures.API.controllers;

import org.rug.scalablecomputing.temperatures.API.helpers.retrievers.StationAverageRetriever;
import org.rug.scalablecomputing.temperatures.API.models.StationAverage;
import org.rug.scalablecomputing.temperatures.API.models.Summary;
import org.rug.scalablecomputing.temperatures.API.repositories.AveragesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AveragesController {

    @Autowired
    private StationAverageRetriever stationAverageRetriever;

    @GetMapping("/")
    public Flux<StationAverage> getAverages() {
        return this.stationAverageRetriever.getAll();
    }

}
