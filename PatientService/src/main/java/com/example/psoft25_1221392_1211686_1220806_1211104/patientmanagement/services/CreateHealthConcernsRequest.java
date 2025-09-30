package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateHealthConcernsRequest {

    @NotBlank
    private String description;

    @NotNull
    private LocalDate dateIdentified;

    private String treatment;

    @NotNull
    private boolean stillOngoing;

    private LocalDate dateResolved;
}

