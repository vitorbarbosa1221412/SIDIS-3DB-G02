package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class InsuranceInformation {

    private String insuranceCompanyName;
    private String policyNumber;

    protected InsuranceInformation() {}

    public InsuranceInformation(String insuranceCompanyName, String policyNumber) {
        this.insuranceCompanyName = (insuranceCompanyName != null) ? insuranceCompanyName.trim() : null;
        this.policyNumber = (policyNumber != null) ? policyNumber.trim() : null;
    }

    @Override
    public String toString() {
        return insuranceCompanyName + " - " + policyNumber;
    }
}

