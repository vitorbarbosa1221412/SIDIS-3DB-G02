package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.services;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UpdateAppointmentRequest {
    @NotNull
    private LocalDateTime dateTime;
}
