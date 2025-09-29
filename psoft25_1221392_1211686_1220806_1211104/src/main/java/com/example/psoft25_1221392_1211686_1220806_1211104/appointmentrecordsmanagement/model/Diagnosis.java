package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model;


import jakarta.persistence.Embeddable;

@Embeddable
public class Diagnosis {
    private String diagnosis;

    public Diagnosis() {}

    public Diagnosis(String diagnosis) {
        if (diagnosis == null || diagnosis.isBlank()) {
            throw new IllegalArgumentException("Diagnosis must not be empty.");
        }
        this.diagnosis = diagnosis;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}
