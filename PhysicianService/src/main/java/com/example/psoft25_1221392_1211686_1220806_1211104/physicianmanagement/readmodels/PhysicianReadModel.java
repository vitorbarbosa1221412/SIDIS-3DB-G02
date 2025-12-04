package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.readmodels;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Modelo de Leitura (Read Model) para o CQRS.
 * Representa o documento no MongoDB (Read DB).
 */
@Document(collection = "physicians_read")
public class PhysicianReadModel {

    // O ID da entidade no MongoDB. Usamos o ID do PostgreSQL para manter a rastreabilidade.
    @Id
    private Long id;

    private String physicianNumber;
    private String name;
    private String specialty;
    // Adicione outros campos necessários que serão exibidos nas consultas

    // Construtores
    public PhysicianReadModel() {}

    public PhysicianReadModel(Long id, String physicianNumber, String name, String specialty) {
        this.id = id;
        this.physicianNumber = physicianNumber;
        this.name = name;
        this.specialty = specialty;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPhysicianNumber() { return physicianNumber; }
    public void setPhysicianNumber(String physicianNumber) { this.physicianNumber = physicianNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}
