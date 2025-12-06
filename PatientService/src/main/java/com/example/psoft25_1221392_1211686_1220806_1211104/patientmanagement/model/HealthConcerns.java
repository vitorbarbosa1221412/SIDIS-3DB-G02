package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class HealthConcerns {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "A descrição não pode estar vazia.")
    private String description;

    @NotNull(message = "A data em que foi identificado é obrigatória.")
    private LocalDate dateIdentified;

    private String treatment;

    @NotNull(message = "O estado atual (stillOngoing) é obrigatório.")
    private boolean stillOngoing;

    private LocalDate dateResolved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({"healthConcerns", "user"})
    private Patient patient;

    // Construtores
    public HealthConcerns() {}

    public HealthConcerns(String description, LocalDate dateIdentified, String treatment,
                          boolean stillOngoing, LocalDate dateResolved, Patient patient) {
        this.description = description;
        this.dateIdentified = dateIdentified;
        this.treatment = treatment;
        this.stillOngoing = stillOngoing;
        this.dateResolved = dateResolved;
        this.patient = patient;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateIdentified() {
        return dateIdentified;
    }

    public void setDateIdentified(LocalDate dateIdentified) {
        this.dateIdentified = dateIdentified;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public boolean isStillOngoing() {
        return stillOngoing;
    }

    public void setStillOngoing(boolean stillOngoing) {
        this.stillOngoing = stillOngoing;
    }

    public LocalDate getDateResolved() {
        return dateResolved;
    }

    public void setDateResolved(LocalDate dateResolved) {
        this.dateResolved = dateResolved;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "HealthConcerns{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", dateIdentified=" + dateIdentified +
                ", treatment='" + treatment + '\'' +
                ", stillOngoing=" + stillOngoing +
                ", dateResolved=" + dateResolved +
                ", patient=" + (patient != null ? patient.getId() : null) +
                '}';
    }
}

