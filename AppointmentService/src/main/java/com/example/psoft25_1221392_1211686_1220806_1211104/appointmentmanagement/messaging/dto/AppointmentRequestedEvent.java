package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.dto;

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
public class AppointmentRequestedEvent implements Serializable {
    private Long appointmentNumber;
    private String patientId;
    private String physicianNumber;
    private LocalDateTime timestamp;
    private String instanceId;
}

