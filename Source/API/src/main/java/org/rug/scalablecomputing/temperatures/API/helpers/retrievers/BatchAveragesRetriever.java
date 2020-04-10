package org.rug.scalablecomputing.temperatures.API.helpers.retrievers;

import org.rug.scalablecomputing.temperatures.API.helpers.Conversions;
import org.rug.scalablecomputing.temperatures.API.models.averages.DailyAverage;
import org.rug.scalablecomputing.temperatures.API.models.averages.MonthlyAverage;
import org.rug.scalablecomputing.temperatures.API.models.averages.WeeklyAverage;
import org.rug.scalablecomputing.temperatures.API.repositories.batchaverages.DailyAveragesRepository;
import org.rug.scalablecomputing.temperatures.API.repositories.batchaverages.MonthlyAveragesRepository;
import org.rug.scalablecomputing.temperatures.API.repositories.batchaverages.WeeklyAveragesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Component
public class BatchAveragesRetriever {

    @Autowired
    private DailyAveragesRepository dailyAveragesRepository;

    @Autowired
    private WeeklyAveragesRepository weeklyAverageRepository;

    @Autowired
    private MonthlyAveragesRepository monthlyAveragesRepository;

    public Mono<List<WeeklyAverage>> getWeeklyAverage(int station, LocalDate fromDate, LocalDate toDate) {
        Mono<List<WeeklyAverage>> weeklyAverageFlux;
        if(fromDate == null) {
            // No fromDate is specified
            weeklyAverageFlux = weeklyAverageRepository.findByStation(station).collectList();
        } else if (toDate == null) {
            // No toDate is specified
            weeklyAverageFlux = weeklyAverageRepository.findByStationAndDateGreaterThanEqual(station, Conversions.LocalDateToInstant(fromDate)).collectList();
        } else {
            // All weekly/monthly averages are only stored using their start date
            // So we adjust for that. In other words:
            // When querying between 01 Jan. and 03 Jan,
            // the week and month is not complete so we dont include these.
            weeklyAverageFlux = weeklyAverageRepository.findByStationAndDateGreaterThanEqualAndDateLessThanEqual(station, Conversions.LocalDateToInstant(fromDate),
                    Conversions.LocalDateToInstant(toDate.minusDays(6))).collectList();
        }
        return weeklyAverageFlux;
    }

    public Mono<List<MonthlyAverage>> getMonthlyAverage(int station, LocalDate fromDate, LocalDate toDate) {
        Mono<List<MonthlyAverage>> monthlyAverageFlux;
        if(fromDate == null) {
            // No fromDate is specified
            monthlyAverageFlux = monthlyAveragesRepository.findByStation(station).collectList();
        } else if (toDate == null) {
            // No toDate is specified
            monthlyAverageFlux = monthlyAveragesRepository.findByStationAndDateGreaterThanEqual(station, Conversions.LocalDateToInstant(fromDate)).collectList();
        } else {
            // All weekly/monthly averages are only stored using their start date
            // So we adjust for that. In other words:
            // When querying between 01 Jan. and 03 Jan,
            // the week and month is not complete so we dont include these.
            monthlyAverageFlux = monthlyAveragesRepository.findByStationAndDateGreaterThanEqualAndDateLessThanEqual(station, Conversions.LocalDateToInstant(fromDate),
                    Conversions.LocalDateToInstant(toDate.minusMonths(1).plusDays(1))).collectList();
        }
        return monthlyAverageFlux;
    }

    public Mono<List<DailyAverage>> getDailyAverages(int station, LocalDate fromDate, LocalDate toDate)
    {
        Mono<List<DailyAverage>> dailyAverageFlux;
        if(fromDate == null) {
            // No fromDate is specified
            dailyAverageFlux = dailyAveragesRepository.findByStation(station).collectList();
        } else if (toDate == null) {
            // No toDate is specified
            dailyAverageFlux = dailyAveragesRepository.findByStationAndDateGreaterThanEqual(station, Conversions.LocalDateToInstant(fromDate)).collectList();
        } else {
            dailyAverageFlux = dailyAveragesRepository.findByStationAndDateGreaterThanEqualAndDateLessThanEqual(station, Conversions.LocalDateToInstant(fromDate),
                    Conversions.LocalDateToInstant(toDate)).collectList();
        }
        return dailyAverageFlux;
    }
}