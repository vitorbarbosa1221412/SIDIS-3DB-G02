package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.api;

import lombok.Getter;

@Getter
public class PhysicianAverageDurationDTO {
    private String physicianNumber;
    private double averageDurationSeconds;

    public PhysicianAverageDurationDTO(String physicianNumber, double averageDurationSeconds) {
        this.physicianNumber = physicianNumber;
        this.averageDurationSeconds = averageDurationSeconds;
    }
}