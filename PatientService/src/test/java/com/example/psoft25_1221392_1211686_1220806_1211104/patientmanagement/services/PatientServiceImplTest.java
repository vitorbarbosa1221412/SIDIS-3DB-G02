package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.ConflictException;
import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.repositories.PatientRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.repositories.UserRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils; // Import Reflection utility
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// --- Dummy classes defined outside the main test class to avoid classpath conflicts ---

class Page {
    public Page(int i, int j) {}
}

class CreatePatientRequest {
    private String emailAddress;
    private String name;
    private String dateOfBirth;
    private String phoneNumber;
    private String address;
    private String insuranceInformation;
    private String dataConsent;
    private boolean enabled;
    private String password;
    private String rePassword;

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getInsuranceInformation() { return insuranceInformation; }
    public void setInsuranceInformation(String insuranceInformation) { this.insuranceInformation = insuranceInformation; }
    public String getDataConsent() { return dataConsent; }
    public void setDataConsent(String dataConsent) { this.dataConsent = dataConsent; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRePassword() { return rePassword; }
    public void setRePassword(String rePassword) { this.rePassword = rePassword; }
}

class UpdatePatientRequest {
    private String phoneNumber;
    private String address;

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}

class PatientEditMapper {
    Patient create(CreatePatientRequest request) {
        throw new UnsupportedOperationException("Mock method should be stubbed");
    }
}

// --- End of dummy classes ---

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientEditMapper mapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Mocks for internal dependencies
    @Mock
    private com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Password passVal;
    @Mock
    private com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.PatientNumber patientNumGen;
    @Mock
    private com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Name nameVal;

    @InjectMocks
    private PatientServiceImpl patientService;

    private CreatePatientRequest createPatientRequest;

    // Mock entities instead of instantiating them
    @Mock
    private Patient patient;
    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        createPatientRequest = new CreatePatientRequest();
        createPatientRequest.setEmailAddress("patient@example.com");
        createPatientRequest.setName("John Doe");
        createPatientRequest.setDateOfBirth("1990-01-01");
        createPatientRequest.setPhoneNumber("123456789");
        createPatientRequest.setAddress("123 Main St");
        createPatientRequest.setInsuranceInformation("Insurance Info");
        createPatientRequest.setDataConsent("YES");
        createPatientRequest.setEnabled(true);
        createPatientRequest.setPassword("Password1");
        createPatientRequest.setRePassword("Password1");

        // FIX: Use Reflection to inject the mock dependencies, overriding the 'new' instantiation
        // in PatientServiceImpl.java, thus solving the "zero interactions" error.
        ReflectionTestUtils.setField(patientService, "passVal", passVal);
        ReflectionTestUtils.setField(patientService, "patientNumGen", patientNumGen);
        ReflectionTestUtils.setField(patientService, "nameVal", nameVal);
    }

    // Helper method to set up common mock patient behavior for success cases
    private void setupMockPatientSuccess() {
        // FIX: Use lenient().when() for all general stubbing to prevent UnnecessaryStubbingException
        lenient().when(patient.getId()).thenReturn(1L);
        lenient().when(patient.getEmailAddress()).thenReturn("patient@example.com");
        lenient().when(patient.getName()).thenReturn("John Doe");
        lenient().when(patient.getDateOfBirth()).thenReturn("1990-01-01");
        lenient().when(patient.getPhoneNumber()).thenReturn("123456789");
        lenient().when(patient.getAddress()).thenReturn("123 Main St");
        lenient().when(patient.getInsuranceInformation()).thenReturn("Insurance Info");
        lenient().when(patient.getDataConsent()).thenReturn("YES");
        lenient().when(patient.getPatientNumber()).thenReturn("PT-2025-1");
        lenient().when(patient.getUser()).thenReturn(user);

        lenient().when(user.getId()).thenReturn(1L);
        lenient().when(user.getUsername()).thenReturn("patient@example.com");
        lenient().when(user.getPassword()).thenReturn("encodedPassword");
        lenient().when(user.isEnabled()).thenReturn(true);
    }


    @Test
    void createPatient_Success() {
        // Arrange
        setupMockPatientSuccess();
        when(patientNumGen.getNextPatientNumber(patientRepository)).thenReturn(1L);
        when(patientNumGen.generate(1L)).thenReturn("PT-2025-1");

        String password = createPatientRequest.getPassword();
        // Stub passVal to succeed
        when(passVal.validate(password)).thenReturn(true);

        when(patientRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());

        when(mapper.create(any(CreatePatientRequest.class))).thenReturn(patient);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        Patient result = patientService.createPatient(createPatientRequest);

        // Assert
        assertNotNull(result);
        assertEquals("patient@example.com", result.getEmailAddress());
        // Verify interaction with passVal is now correctly recorded
        verify(passVal, times(1)).validate(password);
        verify(patientNumGen, times(1)).generate(1L);
        verify(patientRepository, times(1)).findByEmailAddress(anyString());
        verify(mapper, times(1)).create(any(CreatePatientRequest.class));
        verify(patientRepository, times(1)).save(patient);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createPatient_EmailAlreadyExists_ThrowsConflictException() {
        // Arrange
        when(patientRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(patient));

        // Act & Assert
        assertThrows(ConflictException.class, () -> patientService.createPatient(createPatientRequest));
        verify(patientRepository, times(1)).findByEmailAddress(anyString());
        verify(mapper, never()).create(any(CreatePatientRequest.class));
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void createPatient_PasswordsDoNotMatch_ThrowsValidationException() {
        // Arrange
        createPatientRequest.setRePassword("DifferentPassword");
        when(patientRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> patientService.createPatient(createPatientRequest));

        assertEquals("Passwords don't match!", exception.getMessage());
        verify(patientRepository, times(1)).findByEmailAddress(anyString());
        verify(passVal, never()).validate(anyString());
        verify(mapper, never()).create(any(CreatePatientRequest.class));
    }

    @Test
    void createPatient_InvalidPassword_ThrowsValidationException() {
        // Arrange
        when(patientRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());
        String password = "short";
        createPatientRequest.setPassword(password);
        createPatientRequest.setRePassword(password);

        // Stub password validation to fail
        when(passVal.validate(password)).thenReturn(false);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> patientService.createPatient(createPatientRequest));
        assertTrue(exception.getMessage().contains("Password is not valid!"));

        // Verify passVal was called
        verify(passVal, times(1)).validate(password);
        verify(patientRepository, times(1)).findByEmailAddress(anyString());
        verify(mapper, never()).create(any(CreatePatientRequest.class));
    }

    @Test
    void getPatientById_Success() {
        // Arrange
        Long patientId = 1L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        // Act
        ResponseEntity<Patient> response = patientService.getPatientById(patientId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patient, response.getBody());
        verify(patientRepository, times(1)).findById(patientId);
    }

    @Test
    void getPatientById_NotFound_ThrowsResponseStatusException() {
        // Arrange
        Long patientId = 999L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> patientService.getPatientById(patientId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Patient not found"));
        verify(patientRepository, times(1)).findById(patientId);
    }

    @Test
    void getPatientByNumber_Success() {
        // Arrange
        String patientNumber = "PT-2025-1";
        when(patientRepository.findByPatientNumber(patientNumber)).thenReturn(Optional.of(patient));

        // Act
        ResponseEntity<Patient> response = patientService.getPatientByNumber(patientNumber);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patient, response.getBody());
        verify(patientRepository, times(1)).findByPatientNumber(patientNumber);
    }

    @Test
    void getPatientByNumber_NotFound_ReturnsNotFound() {
        // Arrange
        String patientNumber = "PT-2025-999";
        when(patientRepository.findByPatientNumber(patientNumber)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Patient> response = patientService.getPatientByNumber(patientNumber);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(patientRepository, times(1)).findByPatientNumber(patientNumber);
    }

    @Test
    void getPatientByNumber_NullNumber_ReturnsBadRequest() {
        // Act
        ResponseEntity<Patient> response = patientService.getPatientByNumber(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(patientRepository, never()).findByPatientNumber(anyString());
    }

    @Test
    void getPatientByNumber_BlankNumber_ReturnsBadRequest() {
        // Act
        ResponseEntity<Patient> response = patientService.getPatientByNumber("   ");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(patientRepository, never()).findByPatientNumber(anyString());
    }

    @Test
    void searchByPatientName_Success() {
        // Arrange
        String name = "John";
        Page page = new Page(1, 5);
        List<Patient> patients = List.of(patient);

        // Stub name validation to succeed
        when(nameVal.validate(name)).thenReturn(true);
        when(patientRepository.findByName(name)).thenReturn(patients);

        // Act
        List<Patient> result = patientService.searchByPatientName(name, page);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(patient, result.get(0));
        // Verify name validation was called
        verify(nameVal, times(1)).validate(name);
        verify(patientRepository, times(1)).findByName(name);
    }

    @Test
    void searchByPatientName_NullPage_UsesDefaultPage() {
        // Arrange
        String name = "John";
        List<Patient> patients = List.of(patient);

        // Stub name validation to succeed
        when(nameVal.validate(name)).thenReturn(true);
        when(patientRepository.findByName(name)).thenReturn(patients);

        // Act
        List<Patient> result = patientService.searchByPatientName(name, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        // Verify name validation was called
        verify(nameVal, times(1)).validate(name);
        verify(patientRepository, times(1)).findByName(name);
    }

    @Test
    void searchByPatientName_InvalidName_ThrowsValidationException() {
        // Arrange
        String name = "John@#$%";
        Page page = new Page(1, 5);

        // Stub name validation to fail
        when(nameVal.validate(name)).thenReturn(false);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> patientService.searchByPatientName(name, page));
        // Verify name validation was called
        verify(nameVal, times(1)).validate(name);
        assertTrue(exception.getMessage().contains("Name is not valid!"));
        verify(patientRepository, never()).findByName(anyString());
    }

    @Test
    void searchByPatientName_TooLongName_ThrowsValidationException() {
        // Arrange
        String name = "A".repeat(151); // Exceeds 150 character limit
        Page page = new Page(1, 5);

        // Stub name validation to fail
        when(nameVal.validate(name)).thenReturn(false);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> patientService.searchByPatientName(name, page));
        // Verify name validation was called
        verify(nameVal, times(1)).validate(name);
        assertTrue(exception.getMessage().contains("Name is not valid!"));
        verify(patientRepository, never()).findByName(anyString());
    }

    @Test
    void searchByPatientName_NotFound_ThrowsNotFoundException() {
        // Arrange
        String name = "NonExistent";
        Page page = new Page(1, 5);

        // Stub name validation to succeed, allowing it to proceed to the repository call
        when(nameVal.validate(name)).thenReturn(true);
        when(patientRepository.findByName(name)).thenReturn(new ArrayList<>());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> patientService.searchByPatientName(name, page));
        // Verify name validation was called
        verify(nameVal, times(1)).validate(name);
        assertTrue(exception.getMessage().contains("Patient with name " + name + " not found"));
        verify(patientRepository, times(1)).findByName(name);
    }

    @Test
    void getAllPatients_Success() {
        // Arrange
        List<Patient> patients = List.of(patient);
        when(patientRepository.findAll()).thenReturn(patients);

        // Act
        List<Patient> result = patientService.getAllPatients();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(patient, result.get(0));
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getAllPatients_EmptyList_ReturnsEmpty() {
        // Arrange
        when(patientRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Patient> result = patientService.getAllPatients();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void updatePersonalData_Success() {
        // Arrange
        String username = "patient@example.com";
        UpdatePatientRequest request = new UpdatePatientRequest();
        request.setPhoneNumber("987654321");
        request.setAddress("456 New St");

        // Mock the patient lookup
        when(patientRepository.findByUserUsername(username)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        patientService.updatePersonalData(username, request);

        // Assert
        // Verify the setters were called on the mock object
        verify(patient, times(1)).setPhoneNumber("987654321");
        verify(patient, times(1)).setAddress("456 New St");
        verify(patientRepository, times(1)).findByUserUsername(username);
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void updatePersonalData_PatientNotFound_ThrowsRuntimeException() {
        // Arrange
        String username = "nonexistent@example.com";
        UpdatePatientRequest request = new UpdatePatientRequest();
        request.setPhoneNumber("987654321");
        request.setAddress("456 New St");

        when(patientRepository.findByUserUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> patientService.updatePersonalData(username, request));
        assertEquals("Patient not found", exception.getMessage());
        verify(patientRepository, times(1)).findByUserUsername(username);
        verify(patientRepository, never()).save(any(Patient.class));
    }
}