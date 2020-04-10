package org.rug.scalablecomputing.temperatures.API.helpers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class Conversions {
    public static Instant LocalDateToInstant(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}
