package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private String appointmentNumber;
    private Long patientId;
    private Long physicianId;
    private String appointmentDate;
    private String startTime;
    private String endTime;
    private String status;
    private String notes;

	private String patientNumber;

}








