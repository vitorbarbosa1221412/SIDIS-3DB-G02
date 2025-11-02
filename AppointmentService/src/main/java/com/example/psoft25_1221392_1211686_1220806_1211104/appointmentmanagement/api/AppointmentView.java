package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentView {
    private String appointmentNumber;
    private String patientId;
    private String physicianNumber;
    private LocalDateTime appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String notes;
}

