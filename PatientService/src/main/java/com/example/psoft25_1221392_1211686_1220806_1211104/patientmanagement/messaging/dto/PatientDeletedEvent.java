package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event DTO for Patient Deleted
 * Published when a patient is deleted (CQRS event)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDeletedEvent implements Serializable {
    private Long patientId;
    private String patientNumber;
    private LocalDateTime timestamp;
}

