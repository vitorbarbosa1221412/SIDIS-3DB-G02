package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Embeddable
@Getter
public class DateOfBirth {

    @NotNull
    private LocalDate value;

    protected DateOfBirth() {}

    public DateOfBirth(LocalDate value) {
        if (value == null || value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth must be in the past");
        }
        this.value = value;
    }

    public int getAge() {
        return LocalDate.now().getYear() - value.getYear(); // simples, mas pode ser ajustado
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
