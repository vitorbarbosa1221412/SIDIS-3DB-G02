package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class CreatePatientRequest {

    @NonNull
    @NotBlank
    @Email
    private String emailAddress;

    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @NotBlank
    private String dateOfBirth;

    @NonNull
    @NotBlank
    private String phoneNumber;

    @NonNull
    @NotBlank
    private String dataConsent;

    @NonNull
    @NotBlank
    private String password;

    @NonNull
    @NotBlank
    private String rePassword;


    private MultipartFile profilePicture;


    private String patientNumber;

    @NonNull
    @NotBlank
    private String address;

    @NonNull
    private Boolean enabled;

    @NonNull
    @NotBlank
    private String insuranceInformation;

    private List<CreateHealthConcernsRequest> healthConcerns = new ArrayList<>();

    private Set<String> interestList = new HashSet<>();

    private Set<String> authorities = new HashSet<>();
}

