package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRecordDTO {
    private String recordNumber;
    private String appointmentNumber;

    private String diagnosis;
    private String treatmentRecommendation;
    private String prescription;
}
