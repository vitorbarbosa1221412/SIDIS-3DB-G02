package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.handlers;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.QueryHandler;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api.PhysicianDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.GetPhysicianByNumberQuery;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.readmodels.PhysicianReadModel;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianReadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetPhysicianByNumberQueryHandler implements QueryHandler<GetPhysicianByNumberQuery, Optional<PhysicianDTO>> {

    private final PhysicianReadRepository physicianReadRepository;

    @Autowired
    public GetPhysicianByNumberQueryHandler(PhysicianReadRepository physicianReadRepository) {
        this.physicianReadRepository = physicianReadRepository;
    }

    @Override
    public Optional<PhysicianDTO> handle(GetPhysicianByNumberQuery query) {

        // Acesso ao MongoDB usando o método que adicionamos ao ReadRepository
        Optional<PhysicianReadModel> readModel = physicianReadRepository.findByPhysicianNumber(query.getPhysicianNumber());

        // Mapeamento para DTO
        return readModel.map(this::mapToPhysicianDTO);
    }

    private PhysicianDTO mapToPhysicianDTO(PhysicianReadModel model) {
        // Usa a lógica de mapeamento definida anteriormente
        return new PhysicianDTO(
                model.getId(),
                model.getPhysicianNumber(),
                model.getName(),
                model.getSpecialty()
        );
    }
}
