package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Query;


public class GetPhysicianWorkingHoursQuery implements Query<String> {

    private final String physicianNumber;

    public GetPhysicianWorkingHoursQuery(String physicianNumber) {
        this.physicianNumber = physicianNumber;
    }

    public String getPhysicianNumber() {
        return physicianNumber;
    }
}