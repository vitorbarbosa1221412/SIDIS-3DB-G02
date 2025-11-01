package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.api;


import java.util.Set;

/**
 * CONTRATO DE DADOS: Réplica do DTO interno usado para comunicação Auth <-> Resource Server.
 * Usado pelo PatientController para enviar credenciais de volta ao AuthService.
 */
public record UserInternalDTO(
        String id,
        String username,
        String passwordHash,
        Set<String> roles
) {}