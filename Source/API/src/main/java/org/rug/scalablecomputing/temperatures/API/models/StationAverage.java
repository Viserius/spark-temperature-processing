package org.rug.scalablecomputing.temperatures.API.models;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("averages")
public class StationAverage {
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private int station;

    private double daily_average;

    private double weekly_average;

    private double monthly_average;

    public StationAverage(int station, double daily_average, double weekly_average, double monthly_average) {
        this.station = station;
        this.daily_average = daily_average;
        this.weekly_average = weekly_average;
        this.monthly_average = monthly_average;
    }

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "StationAverage{" +
                "station=" + station +
                ", daily_average=" + daily_average +
                ", weekly_average=" + weekly_average +
                ", monthly_average=" + monthly_average +
                '}';
    }

    public double getDaily_average() {
        return daily_average;
    }

    public void setDaily_average(double daily_average) {
        this.daily_average = daily_average;
    }

    public double getWeekly_average() {
        return weekly_average;
    }

    public void setWeekly_average(double weekly_average) {
        this.weekly_average = weekly_average;
    }

    public double getMonthly_average() {
        return monthly_average;
    }

    public void setMonthly_average(double monthly_average) {
        this.monthly_average = monthly_average;
    }
}
