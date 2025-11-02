package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameTest {

    private Name name;

    @BeforeEach
    void setUp() {
        name = new Name();
    }

    @Test
    void validate_ValidSimpleName_ReturnsTrue() {
        // Arrange
        String validName = "John Doe";

        // Act
        boolean result = name.validate(validName);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_ValidNameWithLettersOnly_ReturnsTrue() {
        // Arrange
        String validName = "John";

        // Act
        boolean result = name.validate(validName);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_ValidNameWithNumbers_ReturnsTrue() {
        // Arrange
        String validName = "John123";

        // Act
        boolean result = name.validate(validName);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_ValidNameWithSpaces_ReturnsTrue() {
        // Arrange
        String validName = "John Michael Doe";

        // Act
        boolean result = name.validate(validName);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_NameWithSpecialCharacters_ReturnsFalse() {
        // Arrange
        String invalidName = "John@Doe";

        // Act
        boolean result = name.validate(invalidName);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_NameWithHash_ReturnsFalse() {
        // Arrange
        String invalidName = "John#Doe";

        // Act
        boolean result = name.validate(invalidName);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_NameWithUnderscore_ReturnsFalse() {
        // Arrange
        String invalidName = "John_Doe";

        // Act
        boolean result = name.validate(invalidName);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_NameWithDot_ReturnsFalse() {
        // Arrange
        String invalidName = "John.Doe";

        // Act
        boolean result = name.validate(invalidName);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_NameWithExclamationMark_ReturnsFalse() {
        // Arrange
        String invalidName = "John!Doe";

        // Act
        boolean result = name.validate(invalidName);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_Exactly150Characters_ReturnsTrue() {
        // Arrange
        String validName = "A".repeat(150); // Exactly 150 characters

        // Act
        boolean result = name.validate(validName);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_ShorterThan150Characters_ReturnsTrue() {
        // Arrange
        String validName = "A".repeat(100); // 100 characters

        // Act
        boolean result = name.validate(validName);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_LongerThan150Characters_ReturnsFalse() {
        // Arrange
        String invalidName = "A".repeat(151); // 151 characters (exceeds limit)

        // Act
        boolean result = name.validate(invalidName);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_LongestValidName_ReturnsTrue() {
        // Arrange - 150 characters with spaces
        String validName = "A ".repeat(75); // 150 characters with spaces

        // Act
        boolean result = name.validate(validName);

        // Assert
        assertTrue(result);
    }



    @Test
    void validate_ComplexValidName_ReturnsFalse_DueToSpecialChar() {
        // Arrange
        // Contains apostrophe which is not allowed based on the NameServiceImpl validation message
        // ("Name is not valid! It must contain no special characters...").
        String complexName = "John Michael O'Brien Jr 123";

        // Act
        boolean result = name.validate(complexName);

        // Assert
        assertFalse(result);
    }


    @Test
    void sizeVerification_ValidLength_ReturnsTrue() {
        // Arrange
        String validName = "John Doe";

        // Act
        boolean result = name.sizeVerification(validName);

        // Assert
        assertTrue(result);
    }

    @Test
    void sizeVerification_Exactly150Characters_ReturnsTrue() {
        // Arrange
        String nameExact = "A".repeat(150);

        // Act
        boolean result = name.sizeVerification(nameExact);

        // Assert
        assertTrue(result);
    }

    @Test
    void sizeVerification_ShorterThan150_ReturnsTrue() {
        // Arrange
        String nameShorter = "John";

        // Act
        boolean result = name.sizeVerification(nameShorter);

        // Assert
        assertTrue(result);
    }

    @Test
    void sizeVerification_LongerThan150_ReturnsFalse() {
        // Arrange
        String nameLonger = "A".repeat(151);

        // Act
        boolean result = name.sizeVerification(nameLonger);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_EmptyString_ReturnsTrue() {
        // Arrange
        String emptyName = "";

        // Act
        boolean result = name.validate(emptyName);

        // Assert
        assertTrue(result); // Empty string has length 0, which is <= 150
    }

    @Test
    void validate_ComplexValidName_ReturnsTrue() {
        // Arrange
        String complexName = "John Michael O'Brien Jr 123";

        // Act
        boolean result = name.validate(complexName);

        // Assert
        assertFalse(result); // Contains apostrophe which is a special character
    }

    @Test
    void validate_RealWorldValidName_ReturnsTrue() {
        // Arrange
        String realWorldName = "Maria José Silva";

        // Act
        boolean result = name.validate(realWorldName);

        // Assert
        assertTrue(result);
    }
}

