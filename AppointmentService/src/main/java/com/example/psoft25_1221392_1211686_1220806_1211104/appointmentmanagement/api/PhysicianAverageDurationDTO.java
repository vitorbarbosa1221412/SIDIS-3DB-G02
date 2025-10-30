package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.api;

import lombok.Getter;

@Getter
public class PhysicianAverageDurationDTO {
    private String physicianId;
    private double averageDurationSeconds;

    public PhysicianAverageDurationDTO(String physicianId, double averageDurationSeconds) {
        this.physicianId = physicianId;
        this.averageDurationSeconds = averageDurationSeconds;
    }
}