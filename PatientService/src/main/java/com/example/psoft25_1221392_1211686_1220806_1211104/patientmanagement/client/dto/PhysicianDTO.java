package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhysicianDTO {
    private Long id;
    private String name;
    private String physicianNumber;
    private String emailAddress;
    private String phoneNumber;
    private String address;
    private boolean enabled;
    private String specialization;
    private String department;
}


