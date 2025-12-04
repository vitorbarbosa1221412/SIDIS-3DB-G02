package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.handlers;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.QueryHandler;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.GetPhysicianWorkingHoursQuery;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.readmodels.PhysicianReadModel;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianReadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetPhysicianWorkingHoursQueryHandler implements QueryHandler<GetPhysicianWorkingHoursQuery, String> {

    private final PhysicianReadRepository physicianReadRepository;

    @Autowired
    public GetPhysicianWorkingHoursQueryHandler(PhysicianReadRepository physicianReadRepository) {
        this.physicianReadRepository = physicianReadRepository;
    }

    @Override
    public String handle(GetPhysicianWorkingHoursQuery query) {

        // 1. Acesso ao Read DB (MongoDB)
        // Usamos o método já existente no repositório para buscar pelo physicianNumber
        Optional<PhysicianReadModel> readModel = physicianReadRepository.findByPhysicianNumber(query.getPhysicianNumber());

        // 2. Extrair a informação do Read Model
        return readModel.map(model -> {
            // Assumimos que o PhysicianReadModel tem um campo 'workingHours' (que você deve adicionar)
            // Ou que a lógica está encapsulada noutro campo que representa o horário.
            // Para este exemplo, vou assumir um campo getWorkingHoursString() que você adicionará ao ReadModel.
            // Se o campo não estiver no ReadModel, significa que a projeção falhou em incluir essa informação.

            // Exemplo:
            // return model.getWorkingHoursString();

            // Por enquanto, retorna uma string placeholder até que o campo seja adicionado ao ReadModel
            return "Horas de Trabalho do Médico " + model.getName() + ": 09:00-18:00";

        }).orElse(null); // Retorna null se não encontrar, o Controller lida com o 404
    }
}