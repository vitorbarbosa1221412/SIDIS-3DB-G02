package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api;


public class PhysicianDTO {

    private final Long id;
    private final String physicianNumber;
    private final String name;
    private final String specialty;
    // Adicione outros campos necessários que estão no seu Read Model

    public PhysicianDTO(Long id, String physicianNumber, String name, String specialty) {
        this.id = id;
        this.physicianNumber = physicianNumber;
        this.name = name;
        this.specialty = specialty;
    }

    // Getters para a serialização JSON
    public Long getId() { return id; }
    public String getPhysicianNumber() { return physicianNumber; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }

    // Setters são opcionais se o DTO for imutável
}
