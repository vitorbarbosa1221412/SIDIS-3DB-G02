package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WorkingHours {

    private static final String timeFormat = "HH:mm";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);

    public boolean validate(final String startTime, final String endTime) {
        try {
            LocalTime start = LocalTime.parse(startTime, formatter);
            LocalTime end = LocalTime.parse(endTime, formatter);


            return end.isAfter(start);
        } catch (Exception e) {
            return false;
        }
    }

    public LocalTime parseTime(String timeString) {
        return LocalTime.parse(timeString, formatter);
    }
}

