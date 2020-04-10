package org.rug.scalablecomputing.temperatures.storagemiddleware.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class BatchJobRequest {
    private int station;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public BatchJobRequest(int station, LocalDate startDate, LocalDate endDate) {
        this.station = station;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "BatchJobRequest{" +
                "station=" + station +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
