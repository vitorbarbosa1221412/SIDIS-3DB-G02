package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event DTO for Patient Created
 * Published when a new patient is created (CQRS event)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCreatedEvent implements Serializable {
    private Long patientId;
    private String patientNumber;
    private String emailAddress;
    private String name;
    private LocalDateTime timestamp;
    private String instanceId; // ID of the instance that published this event
}

