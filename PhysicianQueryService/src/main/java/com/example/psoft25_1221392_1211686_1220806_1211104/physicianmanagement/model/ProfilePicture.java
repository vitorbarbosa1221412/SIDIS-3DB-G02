package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Base64;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePicture {

    private String fileName;
    private String contentType;


    private byte[] data;

    public String getBase64Image() {
        if (this.data == null) return null;
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