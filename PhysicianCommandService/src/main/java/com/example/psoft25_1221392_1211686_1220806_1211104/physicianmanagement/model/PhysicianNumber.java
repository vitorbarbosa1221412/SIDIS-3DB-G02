package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class PhysicianNumber {

    private static final String PREFIX = "PH-";          // ← prefixo exclusivo para médicos

    private PhysicianRepository physicianRepository;

    private String getCurrentYear() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        LocalDateTime currentYear = LocalDateTime.now();
        return formatter.format(currentYear);
    }

    public Long getNextPhysicianNumber(PhysicianRepository physicianRepository) {
        String lastNumber = physicianRepository.getLastPhysicianNumber(getCurrentYear());

        if (lastNumber == null || !lastNumber.matches("PH-\\d{4}-\\d+")) {
            return 1L;
        }

        String[] parts = lastNumber.split("-");
        return Long.parseLong(parts[2]) + 1L;
    }






    public String generate(final Long num) {
        String year = getCurrentYear();
        return PREFIX + year + "-" + num;               // PH-2025-3
    }
}
