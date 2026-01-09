package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.events;



import java.io.Serializable;
import java.time.Instant;

public class PatientAssignedToPhysicianEvent implements Serializable {

    private final Long physicianId;
    private final Long patientId;
    private final Instant occurredOn;

    public PatientAssignedToPhysicianEvent(Long physicianId, Long patientId) {
        this.physicianId = physicianId;
        this.patientId = patientId;
        this.occurredOn = Instant.now();
    }

    // Getters
    public Long getPhysicianId() {
        return physicianId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}
