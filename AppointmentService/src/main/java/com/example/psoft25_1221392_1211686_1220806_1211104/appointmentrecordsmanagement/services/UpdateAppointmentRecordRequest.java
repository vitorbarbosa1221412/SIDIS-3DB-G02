package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.services;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class UpdateAppointmentRecordRequest {
    @NonNull
    @NotBlank
    private String diagnosis;

    @NonNull
    @NotBlank
    private String treatmentRecommendation;

    @NonNull
    @NotBlank
    private String prescription;
}
