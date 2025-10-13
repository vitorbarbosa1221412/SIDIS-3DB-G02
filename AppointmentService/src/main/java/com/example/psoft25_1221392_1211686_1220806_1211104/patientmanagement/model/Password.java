package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import java.util.regex.Pattern;

public class Password {
    static final int MIN_LENGTH = 8;
    private Pattern textPattern;
    public boolean upperCaseVerification(final String password){
        textPattern = Pattern.compile("[A-Z]");
        return textPattern.matcher(password).find();
    }

    public boolean numberVerification(final String password){
        textPattern = Pattern.compile("[0-9]");
        return textPattern.matcher(password).find();
    }

    public boolean specialCharacterVerification(final String password){
        textPattern = Pattern.compile("[^a-zA-Z0-9]");
        return textPattern.matcher(password).find();
    }

    public boolean sizeVerification(final String password){

        return password.length() >= MIN_LENGTH;
    }


    public boolean validate(final String password){
        boolean flagUppercase = upperCaseVerification(password);
        boolean flagSpecialCharOrNumber = specialCharacterVerification(password) || numberVerification(password);
        boolean flagSizeVerification = sizeVerification(password);

        return flagUppercase && flagSpecialCharOrNumber && flagSizeVerification;
    }
}
