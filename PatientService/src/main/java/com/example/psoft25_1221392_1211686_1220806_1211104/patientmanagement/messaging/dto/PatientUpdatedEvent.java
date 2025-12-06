package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event DTO for Patient Updated
 * Published when patient data is updated (CQRS event)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdatedEvent implements Serializable {
    private Long patientId;
    private String patientNumber;
    private String phoneNumber;
    private String address;
    private LocalDateTime timestamp;
    private String instanceId; // ID of the instance that published this event
}

