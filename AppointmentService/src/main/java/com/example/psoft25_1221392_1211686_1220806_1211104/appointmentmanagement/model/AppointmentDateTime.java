package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Embeddable
public class AppointmentDateTime {
    private LocalDateTime dateTime;

    public AppointmentDateTime() {}

    public AppointmentDateTime(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date/time must be in the future.");
        }
        this.dateTime = dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isEmpty() {
        return this.dateTime == null;
    }
}
