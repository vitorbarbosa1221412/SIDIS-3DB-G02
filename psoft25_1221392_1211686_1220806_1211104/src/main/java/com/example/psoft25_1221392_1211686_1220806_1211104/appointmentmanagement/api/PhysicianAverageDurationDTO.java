package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


@Getter
public class PhysicianAverageDurationDTO {
    private String physicianName;
    private double averageDurationSeconds;

    public PhysicianAverageDurationDTO(String physicianName, double averageDurationSeconds) {
        this.physicianName = physicianName;
        this.averageDurationSeconds = averageDurationSeconds;
    }

}