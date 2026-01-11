package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientBookedFailEvent implements Serializable {
    private Long appointmentNumber;
    private String reason;
    private LocalDateTime timestamp;
}
