package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    private Password password;

    @BeforeEach
    void setUp() {
        password = new Password();
    }

    @Test
    void validate_ValidPassword_ReturnsTrue() {
        // Arrange
        String validPassword = "Password1";

        // Act
        boolean result = password.validate(validPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_ValidPasswordWithSpecialChar_ReturnsTrue() {
        // Arrange
        String validPassword = "Password@";

        // Act
        boolean result = password.validate(validPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_NoUppercase_ReturnsFalse() {
        // Arrange
        String invalidPassword = "password1";

        // Act
        boolean result = password.validate(invalidPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_NoNumberOrSpecialChar_ReturnsFalse() {
        // Arrange
        String invalidPassword = "Password";

        // Act
        boolean result = password.validate(invalidPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_TooShort_ReturnsFalse() {
        // Arrange
        String invalidPassword = "Pass1"; // Only 5 characters

        // Act
        boolean result = password.validate(invalidPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_Exactly8Characters_ReturnsTrue() {
        // Arrange
        String validPassword = "Passwor1"; // Exactly 8 characters

        // Act
        boolean result = password.validate(validPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_LongerThan8Characters_ReturnsTrue() {
        // Arrange
        String validPassword = "Password12345"; // More than 8 characters

        // Act
        boolean result = password.validate(validPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void upperCaseVerification_HasUppercase_ReturnsTrue() {
        // Arrange
        String passwordWithUpper = "passWord1";

        // Act
        boolean result = password.upperCaseVerification(passwordWithUpper);

        // Assert
        assertTrue(result);
    }

    @Test
    void upperCaseVerification_NoUppercase_ReturnsFalse() {
        // Arrange
        String passwordWithoutUpper = "password1";

        // Act
        boolean result = password.upperCaseVerification(passwordWithoutUpper);

        // Assert
        assertFalse(result);
    }

    @Test
    void numberVerification_HasNumber_ReturnsTrue() {
        // Arrange
        String passwordWithNumber = "Password1";

        // Act
        boolean result = password.numberVerification(passwordWithNumber);

        // Assert
        assertTrue(result);
    }

    @Test
    void numberVerification_NoNumber_ReturnsFalse() {
        // Arrange
        String passwordWithoutNumber = "Password";

        // Act
        boolean result = password.numberVerification(passwordWithoutNumber);

        // Assert
        assertFalse(result);
    }

    @Test
    void specialCharacterVerification_HasSpecialChar_ReturnsTrue() {
        // Arrange
        String passwordWithSpecial = "Password@";

        // Act
        boolean result = password.specialCharacterVerification(passwordWithSpecial);

        // Assert
        assertTrue(result);
    }

    @Test
    void specialCharacterVerification_HasUnderscore_ReturnsTrue() {
        // Arrange
        String passwordWithUnderscore = "Password_";

        // Act
        boolean result = password.specialCharacterVerification(passwordWithUnderscore);

        // Assert
        assertTrue(result);
    }

    @Test
    void specialCharacterVerification_NoSpecialChar_ReturnsFalse() {
        // Arrange
        String passwordWithoutSpecial = "Password1";

        // Act
        boolean result = password.specialCharacterVerification(passwordWithoutSpecial);

        // Assert
        assertFalse(result);
    }

    @Test
    void sizeVerification_ExactlyMinimumLength_ReturnsTrue() {
        // Arrange
        String passwordExact = "Password"; // Exactly 8 characters

        // Act
        boolean result = password.sizeVerification(passwordExact);

        // Assert
        assertTrue(result);
    }

    @Test
    void sizeVerification_LongerThanMinimum_ReturnsTrue() {
        // Arrange
        String passwordLonger = "Password123";

        // Act
        boolean result = password.sizeVerification(passwordLonger);

        // Assert
        assertTrue(result);
    }

    @Test
    void sizeVerification_ShorterThanMinimum_ReturnsFalse() {
        // Arrange
        String passwordShorter = "Pass1"; // Only 5 characters

        // Act
        boolean result = password.sizeVerification(passwordShorter);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_ComplexValidPassword_ReturnsTrue() {
        // Arrange
        String complexPassword = "P@ssw0rd123";

        // Act
        boolean result = password.validate(complexPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_AllRequirementsMet_ReturnsTrue() {
        // Arrange - Has uppercase, has number, meets minimum length
        String validPassword = "MyPass1!";

        // Act
        boolean result = password.validate(validPassword);

        // Assert
        assertTrue(result);
    }
}

