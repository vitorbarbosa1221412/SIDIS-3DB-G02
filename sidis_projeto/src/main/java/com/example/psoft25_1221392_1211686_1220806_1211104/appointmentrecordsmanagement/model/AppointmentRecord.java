package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AppointmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diagnosis;

    private String treatmentRecommendation;

    private String prescription;

    private Long recordNumber;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    public AppointmentRecord() {}

    public AppointmentRecord(String diagnosis, String treatmentRecommendation,
                             String prescription, Appointment appointment,Long recordNumber ) {
        this.diagnosis = diagnosis;
        this.treatmentRecommendation = treatmentRecommendation;
        this.prescription = prescription;
        this.appointment = appointment;
        this.recordNumber = recordNumber;
    }
}
