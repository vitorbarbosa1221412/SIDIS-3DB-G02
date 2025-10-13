package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.ConsultationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleAppointmentRequest {
    @NotBlank
    private String physicianNumber;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    private ConsultationType consultationType;
}
