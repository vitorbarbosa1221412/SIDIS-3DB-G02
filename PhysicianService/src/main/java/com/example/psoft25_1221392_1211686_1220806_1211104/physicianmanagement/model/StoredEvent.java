package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "physician_events")
public class StoredEvent {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "aggregate_id", nullable = false)
    private Long aggregateId;


    @Column(name = "event_type", nullable = false)
    private String eventType;


    @Column(name = "event_data", columnDefinition = "jsonb", nullable = false)
    private String eventData;


    @Column(name = "occurred_on", nullable = false)
    private LocalDateTime occurredOn;

    // Versão do agregado após a aplicação deste evento
    @Column(name = "version", nullable = false)
    private Integer version;



    public StoredEvent() {
        this.occurredOn = LocalDateTime.now();
    }


    public StoredEvent(Long aggregateId, String eventType, String eventData, Integer version) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.eventData = eventData;
        this.version = version;
        this.occurredOn = LocalDateTime.now();
    }



    public Long getId() {
        return id;
    }

    public Long getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventData() {
        return eventData;
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public Integer getVersion() {
        return version;
    }



    public void setId(Long id) {
        this.id = id;
    }

    public void setAggregateId(Long aggregateId) {
        this.aggregateId = aggregateId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public void setOccurredOn(LocalDateTime occurredOn) {
        this.occurredOn = occurredOn;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
