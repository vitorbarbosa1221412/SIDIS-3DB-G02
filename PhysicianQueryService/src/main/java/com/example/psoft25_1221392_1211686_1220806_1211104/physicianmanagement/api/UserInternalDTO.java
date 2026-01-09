package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api;

import java.util.Set;

/**
 * DTO de Autenticação Interna: Contrato de dados usado EXCLUSIVAMENTE
 * para a comunicação Auth Service <-> Resource Server.
 */
// Agora a classe principal é o Record, que gera o construtor automaticamente.
public record UserInternalDTO(
        String id,
        String username,
        String passwordHash, // O hash para o AuthService comparar
        Set<String> roles    // As roles para o JWT
) {
    // Opcional: Pode adicionar métodos aqui, mas o construtor está implícito.
}
