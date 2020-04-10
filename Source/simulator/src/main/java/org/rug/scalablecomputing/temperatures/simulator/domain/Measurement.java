package org.rug.scalablecomputing.temperatures.simulator.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.time.LocalDate;

public class Measurement {
    private int station;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private int hour;
    private int temperature;

    public Measurement(int stationId, LocalDate currentDate, int currentHour, int currentTemperature) {
        this.station = stationId;
        this.date = currentDate;
        this.hour = currentHour;
        this.temperature = currentTemperature;
    }

    public Measurement() {}

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "station=" + station +
                ", date=" + date +
                ", hour=" + hour +
                ", temperature=" + temperature +
                '}';
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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
