package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

import java.time.LocalDate;

@Embeddable
@Setter
public class DataConsent {
    @NotNull
    private boolean consent;

    private LocalDate dateOfConsent;

    public DataConsent() {}

    public DataConsent(LocalDate dateOfConsent) {
        if (dateOfConsent == null || dateOfConsent.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of consent must be in the past");
        }
        this.consent = true;
        this.dateOfConsent = dateOfConsent;
    }

    public void setDate(LocalDate dateOfConsent) {
        this.dateOfConsent = dateOfConsent;
    }
}
