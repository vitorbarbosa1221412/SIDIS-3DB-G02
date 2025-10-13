package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Prescription {
    private String prescription;

    public Prescription() {}

    public Prescription(String prescription) {
        this.prescription = prescription;
    }
}
