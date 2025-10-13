package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhysicianView {
    String name;
    String address;
    String phoneNumber;
    String emailAddress;
    String department;
    String specialty;
    String workingHours;
}
