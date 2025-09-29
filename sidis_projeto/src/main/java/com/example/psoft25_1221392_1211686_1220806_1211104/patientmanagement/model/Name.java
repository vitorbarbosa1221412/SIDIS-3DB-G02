package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.regex.Pattern;

@Embeddable
@Getter
public class Name {
    private Pattern textPattern;
    private final int LENGTH = 150;
    private boolean specialCharacterVerification(final String name){
        textPattern = Pattern.compile("[^a-zA-Z0-9 ]");
        return !textPattern.matcher(name).find();
    }

    public boolean sizeVerification(final String name){

        return name.length() <= LENGTH;
    }

    public boolean validate(final String name){
        boolean flagSpecialCharacters = specialCharacterVerification(name);
        boolean flagSizeVerification = sizeVerification(name);

        return flagSpecialCharacters && flagSizeVerification;
    }
}
