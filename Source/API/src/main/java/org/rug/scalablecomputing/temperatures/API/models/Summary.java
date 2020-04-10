package org.rug.scalablecomputing.temperatures.API.models;

import org.rug.scalablecomputing.temperatures.API.models.averages.DailyAverage;
import org.rug.scalablecomputing.temperatures.API.models.averages.MonthlyAverage;
import org.rug.scalablecomputing.temperatures.API.models.averages.WeeklyAverage;

import java.util.List;

public class Summary {

    private StationAverage stationAverage;
    private List<MonthlyAverage> monthlyAverages;
    private List<WeeklyAverage> weeklyAverages;
    private List<DailyAverage> dailyAverages;
    private List<Measurement> measurements;
    private List<Prediction> predictions;

    public Summary(List<DailyAverage> dailyAverages,
                   List<WeeklyAverage> weeklyAverages,
                   List<MonthlyAverage> monthlyAverage,
                   List<Measurement> measurements,
                   List<Prediction> predictions,
                   StationAverage stationAverage) {
        this.dailyAverages = dailyAverages;
        this.weeklyAverages = weeklyAverages;
        this.monthlyAverages = monthlyAverage;
        this.measurements = measurements;
        this.predictions = predictions;
        this.stationAverage = stationAverage;
    }

    public List<DailyAverage> getDailyAverages() {
        return dailyAverages;
    }

    public void setDailyAverages(List<DailyAverage> dailyAverages) {
        this.dailyAverages = dailyAverages;
    }

    public List<WeeklyAverage> getWeeklyAverages() {
        return weeklyAverages;
    }

    public void setWeeklyAverages(List<WeeklyAverage> weeklyAverages) {
        this.weeklyAverages = weeklyAverages;
    }

    public List<MonthlyAverage> getMonthlyAverages() {
        return monthlyAverages;
    }

    public void setMonthlyAverages(List<MonthlyAverage> monthlyAverages) {
        this.monthlyAverages = monthlyAverages;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    public StationAverage getStationAverage() {
        return stationAverage;
    }

    public void setStationAverage(StationAverage stationAverage) {
        this.stationAverage = stationAverage;
    }
}
