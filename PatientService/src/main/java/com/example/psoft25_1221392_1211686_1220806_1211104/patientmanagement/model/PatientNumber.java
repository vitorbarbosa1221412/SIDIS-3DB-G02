package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.repositories.PatientRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class PatientNumber {

    private static final String PREFIX = "PT-";          // ← prefixo exclusivo para pacientes

    private PatientRepository patientRepository;

    private String getCurrentYear() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        LocalDateTime currentYear = LocalDateTime.now();
        return formatter.format(currentYear);
    }

  /*  public Long getNextPatientNumber(PatientRepository patientRepository) {
        String lastReaderNumber = patientRepository.getLastPatientNumber(getCurrentYear());

        if (lastReaderNumber == null || lastReaderNumber.isBlank()) {
            return 1L;
        }

        String[] parts = lastReaderNumber.split("-");
        if (parts.length < 2) {
            return 1L;
        }

        try {
            return Long.parseLong(parts[1]) + 1L;
        } catch (NumberFormatException e) {
            return 1L; // fallback se não for um número válido
        }
    } */

    public Long getNextPatientNumber(PatientRepository patientRepository) {
        String lastPatientNumber = patientRepository.getLastPatientNumber(getCurrentYear());

        System.out.println("DEBUG - lastPatientNumber: '" + lastPatientNumber + "'");

        if (lastPatientNumber == null || lastPatientNumber.isBlank()) {
            return 1L;
        }

        // O repositório retorna "PT-2025-2026" - usar split com HIFEN
        String[] parts = lastPatientNumber.split("-");

        if (parts.length < 3) {
            return 1L;
        }

        try {
            // parts[0] = "PT", parts[1] = "2025", parts[2] = "2026"
            return Long.parseLong(parts[2]) + 1L;
        } catch (NumberFormatException e) {
            return 1L;
        }
    }


    public String generate(final Long num) {
        String year = getCurrentYear();
        return PREFIX + year + "-" + num;               // PT-2025/7
    }
}
