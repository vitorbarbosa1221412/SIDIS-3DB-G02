package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.events;

import java.io.Serializable;
import java.time.Instant;


public class PhysicianCreatedEvent implements Serializable {


    private final String eventId;


    private final Long physicianId;


    private final String physicianNumber;
    private final String name;
    private final String specialty;


    private final Instant occurredOn;

    public PhysicianCreatedEvent(Long physicianId, String physicianNumber, String name, String specialty) {
        this.eventId = java.util.UUID.randomUUID().toString(); // Gerar um ID único
        this.physicianId = physicianId;
        this.physicianNumber = physicianNumber;
        this.name = name;
        this.specialty = specialty;
        this.occurredOn = Instant.now();
    }


    public String getEventId() {
        return eventId;
    }

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
