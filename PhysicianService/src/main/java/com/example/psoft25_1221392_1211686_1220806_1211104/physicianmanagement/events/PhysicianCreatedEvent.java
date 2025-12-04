package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.events;

import java.io.Serializable;
import java.time.Instant;

/**
 * Evento de Domínio que representa a criação bem-sucedida de um novo Médico (Physician).
 * Será publicado no RabbitMQ.
 */
public class PhysicianCreatedEvent implements Serializable {

    // Identificador único do evento (opcional, mas bom para rastreio)
    private final String eventId;

    // O ID da entidade no Write DB (PostgreSQL)
    private final Long physicianId;

    // Dados essenciais para construir o Read Model (MongoDB)
    private final String physicianNumber;
    private final String name;
    private final String specialty;
    // ... adicione aqui outros campos relevantes para o Read Model (ex: email, dados de imagem)

    private final Instant occurredOn;

    public PhysicianCreatedEvent(Long physicianId, String physicianNumber, String name, String specialty) {
        this.eventId = java.util.UUID.randomUUID().toString(); // Gerar um ID único
        this.physicianId = physicianId;
        this.physicianNumber = physicianNumber;
        this.name = name;
        this.specialty = specialty;
        this.occurredOn = Instant.now();
    }

    // Getters para que o Event Projector (o consumidor) possa aceder aos dados
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
