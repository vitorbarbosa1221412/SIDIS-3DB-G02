package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Query;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api.PhysicianDTO;

import java.util.Optional;

/**
 * Query para obter um Médico pelo seu número de identificação.
 */
public class GetPhysicianByNumberQuery implements Query<Optional<PhysicianDTO>> {

    private final String physicianNumber;

    public GetPhysicianByNumberQuery(String physicianNumber) {
        this.physicianNumber = physicianNumber;
    }

    public String getPhysicianNumber() {
        return physicianNumber;
    }
}
