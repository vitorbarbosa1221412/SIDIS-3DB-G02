package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class TreatmentRecommendation {
    private String recommendation;

    public TreatmentRecommendation() {}

    public TreatmentRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
