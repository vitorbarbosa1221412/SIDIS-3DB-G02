package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.AppointmentClient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.PhysicianClient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.AppointmentDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.AppointmentRecordDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.PhysicianDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientIntegrationServiceTest {

    private static final String TEST_PATIENT_NUMBER = "PT-2025-1";
    @Mock
    private PhysicianClient physicianClient;

    @Mock
    private AppointmentClient appointmentClient;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PatientIntegrationService patientIntegrationService;

    // Use Mockito to provide the Patient instance
    @Mock
    private Patient patient;

    // Initialize DTOs normally as they are simple data holders
    private PhysicianDTO physicianDTO;
    private AppointmentDTO appointmentDTO;
    private AppointmentRecordDTO appointmentRecordDTO;

    @BeforeEach
    void setUp() {
        // NOTE: Mocking for 'patient' object has been removed from setUp()
        // to resolve UnnecessaryStubbingException and is now placed
        // inside the specific test methods that use it.

        // Initialize DTOs for mock responses
        physicianDTO = new PhysicianDTO();
        physicianDTO.setId(1L);
        physicianDTO.setName("Dr. Smith");
        physicianDTO.setPhysicianNumber("PH-2025-1");
        physicianDTO.setEmailAddress("doctor@example.com");
        physicianDTO.setPhoneNumber("123456789");
        physicianDTO.setSpecialization("Cardiology");

        appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAppointmentNumber("APT-2025-1");
        appointmentDTO.setPatientId(1L);
        appointmentDTO.setPhysicianId(1L);
        appointmentDTO.setAppointmentDate("2025-12-31");
        appointmentDTO.setStartTime("10:00");
        appointmentDTO.setEndTime("10:30");
        appointmentDTO.setStatus("SCHEDULED");

        appointmentRecordDTO = new AppointmentRecordDTO();
        appointmentRecordDTO.setRecordNumber("REC-2025-1");
        appointmentRecordDTO.setAppointmentNumber("APT-2025-1");
        appointmentRecordDTO.setDiagnosis("Hypertension");
        appointmentRecordDTO.setTreatmentRecommendation("Medication");
        appointmentRecordDTO.setPrescription("Aspirin");
    }

    @Test
    void getPhysicianById_Success() {
        // Arrange
        Long physicianId = 1L;
        when(physicianClient.getPhysicianById(physicianId)).thenReturn(Optional.of(physicianDTO));

        // Act
        Optional<PhysicianDTO> result = patientIntegrationService.getPhysicianById(physicianId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(physicianDTO, result.get());
        verify(physicianClient, times(1)).getPhysicianById(physicianId);
    }

    @Test
    void getPhysicianById_NotFound_ReturnsEmpty() {
        // Arrange
        Long physicianId = 999L;
        when(physicianClient.getPhysicianById(physicianId)).thenReturn(Optional.empty());

        // Act
        Optional<PhysicianDTO> result = patientIntegrationService.getPhysicianById(physicianId);

        // Assert
        assertFalse(result.isPresent());
        verify(physicianClient, times(1)).getPhysicianById(physicianId);
    }

    @Test
    void isPhysicianAvailable_Available_ReturnsTrue() {
        // Arrange
        String physicianNumber = "PH-2025-1";
        String dateTime = "2025-12-31T10:00";
        when(physicianClient.isPhysicianAvailable(physicianNumber, dateTime)).thenReturn(true);

        // Act
        boolean result = patientIntegrationService.isPhysicianAvailable(physicianNumber, dateTime);

        // Assert
        assertTrue(result);
        verify(physicianClient, times(1)).isPhysicianAvailable(physicianNumber, dateTime);
    }

    @Test
    void isPhysicianAvailable_NotAvailable_ReturnsFalse() {
        // Arrange
        String physicianNumber = "PH-2025-1";
        String dateTime = "2025-12-31T10:00";
        when(physicianClient.isPhysicianAvailable(physicianNumber, dateTime)).thenReturn(false);

        // Act
        boolean result = patientIntegrationService.isPhysicianAvailable(physicianNumber, dateTime);

        // Assert
        assertFalse(result);
        verify(physicianClient, times(1)).isPhysicianAvailable(physicianNumber, dateTime);
    }

    @Test
    void getAppointmentHistory_WithPatient_Success() {
        // Arrange
        List<AppointmentDTO> appointments = List.of(appointmentDTO);

        // Stub the specific method called by the service layer
        when(patient.getPatientNumber()).thenReturn("PT-2025-1");
        when(appointmentClient.getAppointmentHistory("PT-2025-1")).thenReturn(appointments);

        // Act
        List<AppointmentDTO> result = patientIntegrationService.getAppointmentHistory(patient);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointmentDTO, result.get(0));

        verify(patient, times(1)).getPatientNumber();
        verify(appointmentClient, times(1)).getAppointmentHistory(TEST_PATIENT_NUMBER);
    }

    @Test
    void getAppointmentHistory_WithPatientNumber_Success() {
        // Arrange
        String patientNumber = "PT-2025-1";
        List<AppointmentDTO> appointments = List.of(appointmentDTO);
        when(appointmentClient.getAppointmentHistory(patientNumber)).thenReturn(appointments);

        // Act
        List<AppointmentDTO> result = patientIntegrationService.getAppointmentHistory(patientNumber);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointmentDTO, result.get(0));
        verify(appointmentClient, times(1)).getAppointmentHistory(patientNumber);
    }



    @Test
    void getAppointmentByNumber_Success() {
        // Arrange
        String appointmentNumber = "APT-2025-1";
        when(appointmentClient.getAppointmentByNumber(appointmentNumber)).thenReturn(Optional.of(appointmentDTO));

        // Act
        Optional<AppointmentDTO> result = patientIntegrationService.getAppointmentByNumber(appointmentNumber);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(appointmentDTO, result.get());
        verify(appointmentClient, times(1)).getAppointmentByNumber(appointmentNumber);
    }

    @Test
    void getAppointmentByNumber_NotFound_ReturnsEmpty() {
        // Arrange
        String appointmentNumber = "APT-2025-999";
        when(appointmentClient.getAppointmentByNumber(appointmentNumber)).thenReturn(Optional.empty());

        // Act
        Optional<AppointmentDTO> result = patientIntegrationService.getAppointmentByNumber(appointmentNumber);

        // Assert
        assertFalse(result.isPresent());
        verify(appointmentClient, times(1)).getAppointmentByNumber(appointmentNumber);
    }

    @Test
    void createAppointment_Success() {
        // Arrange
        Object appointmentRequest = new Object();
        when(appointmentClient.createAppointment(appointmentRequest)).thenReturn(Optional.of(appointmentDTO));

        // Act
        Optional<AppointmentDTO> result = patientIntegrationService.createAppointment(appointmentRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(appointmentDTO, result.get());
        verify(appointmentClient, times(1)).createAppointment(appointmentRequest);
    }

    @Test
    void createAppointment_Failure_ReturnsEmpty() {
        // Arrange
        Object appointmentRequest = new Object();
        when(appointmentClient.createAppointment(appointmentRequest)).thenReturn(Optional.empty());

        // Act
        Optional<AppointmentDTO> result = patientIntegrationService.createAppointment(appointmentRequest);

        // Assert
        assertFalse(result.isPresent());
        verify(appointmentClient, times(1)).createAppointment(appointmentRequest);
    }

    @Test
    void cancelAppointment_Success_ReturnsTrue() {
        // Arrange
        String appointmentNumber = "APT-2025-1";
        when(appointmentClient.cancelAppointment(appointmentNumber)).thenReturn(true);

        // Act
        boolean result = patientIntegrationService.cancelAppointment(appointmentNumber);

        // Assert
        assertTrue(result);
        verify(appointmentClient, times(1)).cancelAppointment(appointmentNumber);
    }

    @Test
    void cancelAppointment_Failure_ReturnsFalse() {
        // Arrange
        String appointmentNumber = "APT-2025-999";
        when(appointmentClient.cancelAppointment(appointmentNumber)).thenReturn(false);

        // Act
        boolean result = patientIntegrationService.cancelAppointment(appointmentNumber);

        // Assert
        assertFalse(result);
        verify(appointmentClient, times(1)).cancelAppointment(appointmentNumber);
    }

    @Test
    void searchPhysiciansByName_Success() {
        // Arrange
        String name = "Smith";
        PhysicianDTO[] physiciansArray = {physicianDTO};
        when(physicianClient.searchPhysiciansByName(name)).thenReturn(Optional.of(physiciansArray));

        // Act
        List<PhysicianDTO> result = patientIntegrationService.searchPhysiciansByName(name);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(physicianDTO, result.get(0));
        verify(physicianClient, times(1)).searchPhysiciansByName(name);
    }

    @Test
    void searchPhysiciansByName_NotFound_ReturnsEmptyList() {
        // Arrange
        String name = "NonExistent";
        when(physicianClient.searchPhysiciansByName(name)).thenReturn(Optional.empty());

        // Act
        List<PhysicianDTO> result = patientIntegrationService.searchPhysiciansByName(name);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(physicianClient, times(1)).searchPhysiciansByName(name);
    }

    @Test
    void searchPhysiciansBySpecialty_Success() {
        // Arrange
        String specialty = "Cardiology";
        PhysicianDTO[] physiciansArray = {physicianDTO};
        when(physicianClient.searchPhysiciansBySpecialty(specialty)).thenReturn(Optional.of(physiciansArray));

        // Act
        List<PhysicianDTO> result = patientIntegrationService.searchPhysiciansBySpecialty(specialty);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(physicianDTO, result.get(0));
        verify(physicianClient, times(1)).searchPhysiciansBySpecialty(specialty);
    }

    @Test
    void searchPhysiciansBySpecialty_NotFound_ReturnsEmptyList() {
        // Arrange
        String specialty = "NonExistent";
        when(physicianClient.searchPhysiciansBySpecialty(specialty)).thenReturn(Optional.empty());

        // Act
        List<PhysicianDTO> result = patientIntegrationService.searchPhysiciansBySpecialty(specialty);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(physicianClient, times(1)).searchPhysiciansBySpecialty(specialty);
    }

    @Test
    void getAvailableSlots_Success() {
        // Arrange
        String physicianNumber = "PH-2025-1";
        LocalDate date = LocalDate.of(2025, 12, 31);
        List<LocalTime> slots = List.of(LocalTime.of(10, 0), LocalTime.of(11, 0));
        when(appointmentClient.getAvailableSlots(physicianNumber, date)).thenReturn(slots);

        // Act
        List<LocalTime> result = patientIntegrationService.getAvailableSlots(physicianNumber, date);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(slots, result);
        verify(appointmentClient, times(1)).getAvailableSlots(physicianNumber, date);
    }

    @Test
    void getAvailableSlots_NoSlots_ReturnsEmptyList() {
        // Arrange
        String physicianNumber = "PH-2025-1";
        LocalDate date = LocalDate.of(2025, 12, 31);
        when(appointmentClient.getAvailableSlots(physicianNumber, date)).thenReturn(Collections.emptyList());

        // Act
        List<LocalTime> result = patientIntegrationService.getAvailableSlots(physicianNumber, date);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(appointmentClient, times(1)).getAvailableSlots(physicianNumber, date);
    }





    @Test
    void searchAppointmentsByPhysician_Success() {
        // Arrange
        String physicianName = "Dr. Smith";
        List<AppointmentDTO> appointments = List.of(appointmentDTO);
        when(appointmentClient.searchAppointmentsByPhysician(physicianName)).thenReturn(appointments);

        // Act
        List<AppointmentDTO> result = patientIntegrationService.searchAppointmentsByPhysician(physicianName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointmentDTO, result.get(0));
        verify(appointmentClient, times(1)).searchAppointmentsByPhysician(physicianName);
    }

    @Test
    void searchAppointmentsByPhysician_NotFound_ReturnsEmptyList() {
        // Arrange
        String physicianName = "Dr. NonExistent";
        when(appointmentClient.searchAppointmentsByPhysician(physicianName)).thenReturn(Collections.emptyList());

        // Act
        List<AppointmentDTO> result = patientIntegrationService.searchAppointmentsByPhysician(physicianName);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(appointmentClient, times(1)).searchAppointmentsByPhysician(physicianName);
    }

    @Test
    void getAppointmentRecords_Success() {
        // Arrange
        String patientNumber = "PT-2025-1";
        List<AppointmentRecordDTO> records = List.of(appointmentRecordDTO);
        when(appointmentClient.getAppointmentRecords(patientNumber)).thenReturn(records);

        // Act
        List<AppointmentRecordDTO> result = patientIntegrationService.getAppointmentRecords(patientNumber);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointmentRecordDTO, result.get(0));
        verify(appointmentClient, times(1)).getAppointmentRecords(patientNumber);
    }

    @Test
    void getAppointmentRecords_NotFound_ReturnsEmptyList() {
        // Arrange
        String patientNumber = "PT-2025-999";
        when(appointmentClient.getAppointmentRecords(patientNumber)).thenReturn(Collections.emptyList());

        // Act
        List<AppointmentRecordDTO> result = patientIntegrationService.getAppointmentRecords(patientNumber);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(appointmentClient, times(1)).getAppointmentRecords(patientNumber);
    }
}