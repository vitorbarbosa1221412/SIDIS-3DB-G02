package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.CreatePhysicianCommand;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.events.PhysicianCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

public class PhysicianAggregate {

    private Long id;
    private String physicianNumber;
    private String name;
    private String specialty;
    private int version = 0;


    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private PhysicianAggregate() {}

    public static PhysicianAggregate createFrom(List<StoredEvent> history) {
        PhysicianAggregate aggregate = new PhysicianAggregate();
        for (StoredEvent event : history) {
            aggregate.applyEvent(event);
        }
        return aggregate;
    }

    private void applyEvent(StoredEvent event) {
        this.version = event.getVersion();

        switch (event.getEventType()) {
            case "PhysicianCreatedEvent":

                this.id = event.getAggregateId();

                this.name = event.getEventData();
                this.physicianNumber = "P-TEMP";
                this.specialty = "Cardiologia";
                break;
            case "PhysicianUpdatedEvent":
                this.version = event.getVersion();

                break;

        }
    }

    private static final Long NEXT_ID_PLACEHOLDER = 1L;

    public static List<Object> create(Long newAggregateId, CreatePhysicianCommand command) {

        // 1. Gera os dados do evento (obtidos do Command)
        // Usamos os dados do Command
        PhysicianCreatedEvent newEvent = new PhysicianCreatedEvent(
                newAggregateId,
                command.getRequest().getPhysicianNumber(),
                command.getRequest().getName(),
                command.getRequest().getSpecialty()
        );

        // 2. Retorna a lista de eventos gerados
        return List.of(newEvent);
    }


    public Long getId() {
        return id;
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

    public int getVersion() {
        return version;
    }
}
