package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.events;


import java.io.Serializable;
import java.time.Instant;


public class PhysicianUpdatedEvent implements Serializable {

    private final Long physicianId;
    private final String physicianNumber; // Chave de busca
    private final String name;
    private final String specialty;
    private final Instant occurredOn;

    public PhysicianUpdatedEvent(Long physicianId, String physicianNumber, String name, String specialty) {
        this.physicianId = physicianId;
        this.physicianNumber = physicianNumber;
        this.name = name;
        this.specialty = specialty;
        this.occurredOn = Instant.now();
    }

    // Getters para que o Event Projector possa aceder aos dados
    public Long getPhysicianId() {
        return physicianId;
    }

    public String getPhysicianNumber() {
        return physicianNumber;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}
