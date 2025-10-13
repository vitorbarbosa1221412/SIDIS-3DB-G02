package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class CreatePhysicianRequest {
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

    private Set<String> authorities = new HashSet<>();
}
