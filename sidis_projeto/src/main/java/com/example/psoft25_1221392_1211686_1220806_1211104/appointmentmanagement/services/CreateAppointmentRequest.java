package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.services;


import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.ConsultationType;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateAppointmentRequest {

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    private ConsultationType consultationType;

    @NotNull
    private AppointmentStatus status;

    @NotNull
    private Long patientNumber;

    @NotNull
    private Long physicianNumber;
}

