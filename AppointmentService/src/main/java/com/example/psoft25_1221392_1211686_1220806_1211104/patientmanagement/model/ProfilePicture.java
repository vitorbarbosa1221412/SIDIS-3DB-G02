package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;
import java.util.Objects;

@Embeddable
@Getter
@Setter   // adiciona setters aqui
public class ProfilePicture {

    private String fileName;     // Ex: "photo.jpg"
    private String contentType;  // Ex: "image/jpeg"

    @Lob
    @Column(name = "data")
    private byte[] data;         // A imagem em si

    protected ProfilePicture() {
        // JPA needs a no-args constructor
    }

    public ProfilePicture(String fileName, String contentType, byte[] data) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
    }

    public String getBase64Image() {
        return Base64.getEncoder().encodeToString(this.data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfilePicture that)) return false;
        return Objects.equals(fileName, that.fileName) &&
                Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, contentType);
    }
}

