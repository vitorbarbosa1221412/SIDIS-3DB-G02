package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElectronicPrescriptionDTO {
    private String patientId;
    private String physicianId;
    private String prescription;
}
