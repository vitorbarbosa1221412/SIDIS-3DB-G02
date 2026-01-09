package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class UpdatePhysicianRequest {
    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @NotBlank
    private String address;

    @NonNull
    @NotBlank
    private String phoneNumber;

    @NonNull
    @NotBlank
    private String password;

    @NonNull
    @NotBlank
    @Email
    private String emailAddress;

    @NonNull
    @NotBlank
    private String department;

    @NonNull
    @NotBlank
    private String specialty;

    @NonNull
    @NotBlank
    private String workingHours;
}
