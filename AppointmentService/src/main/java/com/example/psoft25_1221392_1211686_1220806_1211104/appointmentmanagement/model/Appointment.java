package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "appointment_number", unique = true))
    private AppointmentNumber appointmentNumber;

    private LocalDateTime dateTime;


    private LocalDateTime startTime;


    private LocalDateTime endTime;


    @Enumerated(EnumType.STRING)
    private ConsultationType consultationType;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @ManyToOne(optional = false)
    private Patient patient;

    @ManyToOne(optional = false)
    private Physician physician;

    // @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
   // private AppointmentRecord appointmentRecord;

    public Appointment() {
        // necessário para o JPA
    }


    public Appointment(LocalDateTime dateTime, ConsultationType consultationType, AppointmentStatus status,
                       Patient patient, Physician physician) {
        this();
        this.dateTime = dateTime;
        this.consultationType = consultationType;
        this.status = status;
        this.patient = patient;
        this.physician = physician;

    }
}
