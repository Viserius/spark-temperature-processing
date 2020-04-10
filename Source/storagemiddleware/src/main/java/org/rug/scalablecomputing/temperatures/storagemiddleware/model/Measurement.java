package org.rug.scalablecomputing.temperatures.storagemiddleware.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.util.Date;

@Table("measurements")
public class Measurement {
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private int station;

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private Date date;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    private int hour;

    private int temperature;

    public Measurement(int stationId, Date date, int currentHour, int currentTemperature) {
        this.station = stationId;
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
