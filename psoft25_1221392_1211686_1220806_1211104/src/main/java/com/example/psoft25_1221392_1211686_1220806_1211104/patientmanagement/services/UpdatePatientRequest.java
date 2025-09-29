package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePatientRequest {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String address;
}