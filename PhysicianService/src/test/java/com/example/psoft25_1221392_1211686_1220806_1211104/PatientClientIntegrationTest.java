package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PatientClientIntegrationTest {

    @Autowired
    private PatientClient patientClient;

    @Test
    void testPatientExists() {
        Long testId = 1L; // coloca um ID que exista no PatientService
        boolean exists = patientClient.patientExists(testId);

        System.out.println("Paciente existe? " + exists);
        assertThat(exists).isTrue(); // ou isFalse(), dependendo do que esperas
    }
}


