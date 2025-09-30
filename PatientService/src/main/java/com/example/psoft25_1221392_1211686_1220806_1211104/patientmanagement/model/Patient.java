package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

//import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.ProfilePicture;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String patientNumber;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_Id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String dateOfBirth;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    private boolean enabled = true;

    @Column(nullable = false)
    private String insuranceInformation;

    @Column(nullable = false)

    private String dataConsent;

    //@Embedded
    //private ProfilePicture profilePicture;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthConcerns> healthConcerns = new ArrayList<>();



    protected Patient() {}

    public Patient(final String emailAddress, String name, final String dateOfBirth, final String phoneNumber, final String address, final String insuranceInformation, final String dataConsent) {
        this.emailAddress = emailAddress;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.insuranceInformation = insuranceInformation;
        this.dataConsent = dataConsent;
    }

    public static Patient newPatient(final String emailAddress, final String name, final String dateOfBirth, final String phoneNumber, final String address, final String insuranceInformation, final String dataConsent, final User user) {
        Patient patient = new Patient(emailAddress, name, dateOfBirth, phoneNumber, address, insuranceInformation, dataConsent);
        patient.setUser(user);
        user.setFullName(name);
        user.addAuthority(new Role(Role.PATIENT));
        return patient;
    }

}

