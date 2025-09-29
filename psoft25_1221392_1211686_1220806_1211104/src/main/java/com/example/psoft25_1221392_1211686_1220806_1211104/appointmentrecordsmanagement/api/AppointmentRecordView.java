package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AppointmentRecordView {
    private String diagnosis;
    private String treatmentRecommendation;
    private String prescription;
    private Long recordNumber;

    // Dados da Appointment
    private String appointmentNumber;
    private LocalDateTime appointmentDateTime;
    private String consultationType;
    private String status;

    // Dados do Patient (limitados)
    private String patientNumber;
    private String patientEmail;

    // Dados do Physician (limitados)
    private String physicianName;
    private String physicianNumber;
}


