package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailAddress {
    @NotBlank(message = "O email não pode ser vazio.")
    @Email(message = "O email deve ser válido.")
    private String emailAddress;

    public EmailAddress() {
    }

    public EmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {
        return "Email [emailAddress=" + emailAddress + "]";
    }
}
