package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Embeddable
@Getter
public class PhoneNumber {

    @Pattern(regexp = "\\d{9}", message = "Phone number must have 9 digits")
    private String value;

    protected PhoneNumber() {}

    public PhoneNumber(String value) {
        if (value == null || !value.matches("\\d{9}")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
