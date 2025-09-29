package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientView {
    private Long id;
    private String name;
    private String username;
    private String phoneNumber;
    private String dateOfBirth;
    private String patientNumber;
    private boolean enabled;
    private List<HealthConcernsView> healthConcerns;

}
