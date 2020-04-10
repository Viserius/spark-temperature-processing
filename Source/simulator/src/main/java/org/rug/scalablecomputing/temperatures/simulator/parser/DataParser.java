package org.rug.scalablecomputing.temperatures.simulator.parser;

import org.rug.scalablecomputing.temperatures.simulator.domain.Measurement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataParser {

    public Measurement parseLine(String data) {
        if(data.charAt(0) == '#')
            return null;

        // Split on commas without any whitespace
        String[] values = data.trim().split("[\\s]*,[\\s]*");

        // Parsing the data into a structure
        Measurement measurement = new Measurement();
        try {
            measurement.setStation(Integer.parseInt(values[0]));
            measurement.setDate(LocalDate.parse(values[1], DateTimeFormatter.ofPattern("yyyyMMdd")));
            measurement.setHour(Integer.parseInt(values[2]));
            measurement.setTemperature(Integer.parseInt(values[3]));
        } catch (Exception e) {
            System.out.println("Could not parse a measurement, invalid or incomplete data.");
            System.out.println(e.getMessage());
            return null;
        }
        return measurement;
    }

}
