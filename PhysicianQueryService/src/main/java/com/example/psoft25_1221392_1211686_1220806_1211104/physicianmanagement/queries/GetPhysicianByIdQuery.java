package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Query;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api.PhysicianDTO; // Seu DTO de retorno

import java.util.Optional;

/**
 * Query para obter um Médico pelo seu ID.
 * Retorna um Optional<PhysicianDTO> (o Read Model/DTO).
 */
public class GetPhysicianByIdQuery implements Query<Optional<PhysicianDTO>> {

    private final Long physicianId;

    public GetPhysicianByIdQuery(Long physicianId) {
        this.physicianId = physicianId;
    }

    public Long getPhysicianId() {
        return physicianId;
    }
}
