package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Department {

    @NotBlank(message = "A sigla do departamento não pode ser vazia.")
    @Size(max = 5, message = "A sigla deve ter no máximo 5 caracteres.")
    private String acronym;

    @NotBlank(message = "O nome do departamento não pode ser vazio.")
    private String name;

    private String description;


    public Department() {
    }

    public Department(String acronym, String name, String description) {
        this.acronym = acronym;
        this.name = name;
        this.description = description;
    }


    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
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
        return "Department [acronym=" + acronym + ", name=" + name + ", description=" + description + "]";
    }
}
