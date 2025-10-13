package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.services;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class CreateAppointmentRecordRequest {

    @NotNull
    @NonNull
    private String diagnosis;

    @NotNull
    @NonNull
    private String treatmentRecommendation;

    @NotNull
    @NonNull
    private String prescription;

    @NotNull
    @NonNull
    private String appointmentNumber; // <- substitui recordNumber

    public CreateAppointmentRecordRequest() {}

    public CreateAppointmentRecordRequest(@NonNull String diagnosis,
                                          @NonNull String treatmentRecommendation,
                                          @NonNull String prescription,
                                          @NonNull String appointmentNumber) {
        if (diagnosis == null) throw new NullPointerException("diagnosis is marked non-null but is null");
        if (treatmentRecommendation == null) throw new NullPointerException("treatmentRecommendation is marked non-null but is null");
        if (prescription == null) throw new NullPointerException("prescription is marked non-null but is null");
        if (appointmentNumber == null) throw new NullPointerException("appointmentNumber is marked non-null but is null");

        this.diagnosis = diagnosis;
        this.treatmentRecommendation = treatmentRecommendation;
        this.prescription = prescription;
        this.appointmentNumber = appointmentNumber;
    }
}

