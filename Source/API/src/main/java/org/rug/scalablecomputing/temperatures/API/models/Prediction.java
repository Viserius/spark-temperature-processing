package org.rug.scalablecomputing.temperatures.API.models;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.Date;

@Table("predictions")
public class Prediction {
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private int station;

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private Instant date;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    private int hour;

    private int temperature;

    public Prediction(int stationId, Instant date, int currentHour, int currentTemperature) {
        this.station = stationId;
        this.date = date;
        this.hour = currentHour;
        this.temperature = currentTemperature;
    }

    public Prediction() {}

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "Prediction{" +
                "station=" + station +
                ", date=" + date +
                ", hour=" + hour +
                ", temperature=" + temperature +
                '}';
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
