package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRequest {
	private String dateTime; // ISO-8601 string expected by Physician Service
}



