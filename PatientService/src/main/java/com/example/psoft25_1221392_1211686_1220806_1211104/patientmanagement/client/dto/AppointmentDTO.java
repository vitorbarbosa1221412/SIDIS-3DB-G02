package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
	private String appointmentNumber;
	private String appointmentDateTime; // ISO-8601 string to avoid cross-service type coupling
	private String consultationType;
	private String status;

	private String patientNumber;
	private String patientEmail;

	private String physicianNumber;
	private String physicianName;

	private String diagnosis;
	private String treatmentRecommendation;
	private String prescription;
	private String recordNumber;
}



