package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import lombok.Data;

@Data
public class AgeGroupStats {

    private String ageGroup;
    private long numberOfAppointments;
    private double averageDurationMinutes;

    public AgeGroupStats(String ageGroup, long numberOfAppointments, double averageDurationMinutes) {
        this.ageGroup = ageGroup;
        this.numberOfAppointments = numberOfAppointments;
        this.averageDurationMinutes = averageDurationMinutes;
    }

}

