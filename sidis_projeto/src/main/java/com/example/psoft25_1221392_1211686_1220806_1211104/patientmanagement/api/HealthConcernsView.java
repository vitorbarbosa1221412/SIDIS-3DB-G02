package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthConcernsView {
    private Long id;
    private String description;
    private String dateIdentified;
    private String treatment;
    private String stillOngoing;
    private String dateResolved;
}
