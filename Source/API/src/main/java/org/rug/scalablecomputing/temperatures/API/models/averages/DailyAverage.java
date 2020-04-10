package org.rug.scalablecomputing.temperatures.API.models.averages;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@Table("batch_averages_daily")
public class DailyAverage {
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private int station;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    private Instant date;

    private int temperature;

    public DailyAverage(int stationId, Instant date, int currentTemperature) {
        this.station = stationId;
        this.date = date;
        this.temperature = currentTemperature;
    }

    public DailyAverage() {}

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "DailyAverage{" +
                "station=" + station +
                ", date=" + date +
                ", temperature=" + temperature +
                '}';
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
