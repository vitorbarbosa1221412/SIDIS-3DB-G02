package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.client.PatientClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PatientClientIntegrationTest {

    @Autowired
    private PatientClient patientClient;

    @Test
    void testConnectionToPatientService() {
        Long testPatientId = 1L; // muda para um ID que exista no teu PatientService

        try {
            var patient = patientClient.getPatientById(testPatientId);
            assertNotNull(patient, "O paciente retornado não deve ser nulo");
            System.out.println("✅ Conexão bem-sucedida com o PatientService. Paciente: " + patient);
        } catch (RestClientException e) {
            fail("❌ Erro ao conectar ao PatientService: " + e.getMessage());
        }
    }
}

