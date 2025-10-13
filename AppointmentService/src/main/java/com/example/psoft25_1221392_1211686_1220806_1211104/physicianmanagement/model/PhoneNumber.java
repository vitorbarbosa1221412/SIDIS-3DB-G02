package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PhoneNumber {

    @NotBlank(message = "O número de telefone não pode ser vazio.")
    @Pattern(regexp = "^\\d{9}$", message = "O número de telefone deve ter exatamente 9 dígitos.")
    private String phoneNumber;

    public PhoneNumber() {
    }

    public PhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "PhoneNumber [phoneNumber=" + phoneNumber + "]";
    }
}
