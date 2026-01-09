package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model;

import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "physicians")
@Getter
@Setter
public class Physician {

    @Id
    private Long id;

    private String name;

    private String address;

    private String physicianNumber;

    // o User será salvo como um sub-documento (objeto JSON aninhado)

    private User user;

    private String phoneNumber;

    private String emailAddress;

    private boolean enabled = true;

    private String department;

    private String specialty;

    private String workingHours;

    private ProfilePicture profilePicture;

    public Physician() {}

    public Physician(Long id, String name, String address, String phoneNumber, String emailAddress,
                     String department, String specialty, String workingHours, ProfilePicture profilePicture) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.department = department;
        this.specialty = specialty;
        this.workingHours = workingHours;
        this.profilePicture = profilePicture;
    }


    @Override
    public String toString() {
        return "Physician [id=" + id +
                ", name=" + name +
                ", address=" + address +
                ", phoneNumber=" + phoneNumber +
                ", emailAddress=" + emailAddress +
                ", specialty=" + specialty +
                ", workingHours=" + workingHours +
                ", profilePicture=" + (profilePicture != null ? profilePicture.getFileName() : "none") +
                "]";
    }
}