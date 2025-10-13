package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable

public class AppointmentNumber {

    private String value;

    protected AppointmentNumber() {
        // Necessário para o JPA
    }

    public AppointmentNumber(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentNumber that = (AppointmentNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
