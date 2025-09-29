package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import jakarta.validation.constraints.NotBlank;

public class Address {

    @NotBlank(message = "A rua não pode ser vazia.")
    private String street;

    @NotBlank(message = "O número não pode ser vazio.")
    private String number;

    private String floor;

    @NotBlank(message = "A cidade não pode ser vazia.")
    private String city;

    public Address() {
    }

    public Address(String street, String number, String floor, String city) {
        this.street = street;
        this.number = number;
        this.floor = floor;
        this.city = city;
    }

    // Getters e Setters
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Address [street=" + street + ", number=" + number +
                ", floor=" + floor + ", city=" + city + "]";
    }
}
