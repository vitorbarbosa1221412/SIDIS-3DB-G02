package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model;

import jakarta.validation.constraints.NotBlank;

public class Specialty {

    @NotBlank(message = "O nome da especialidade não pode ser vazio.")
    private String name;

    private String description;


    public Specialty() {
    }

    public Specialty(String name, String description) {
        this.name = name;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Specialty [name=" + name + ", description=" + description + "]";
    }
}
