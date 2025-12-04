package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.handlers;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.QueryHandler;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api.PhysicianDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.SearchPhysiciansQuery;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.readmodels.PhysicianReadModel;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianReadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchPhysiciansQueryHandler implements QueryHandler<SearchPhysiciansQuery, List<PhysicianDTO>> {

    private final PhysicianReadRepository physicianReadRepository;

    @Autowired
    public SearchPhysiciansQueryHandler(PhysicianReadRepository physicianReadRepository) {
        this.physicianReadRepository = physicianReadRepository;
    }

    @Override
    public List<PhysicianDTO> handle(SearchPhysiciansQuery query) {

        // 1. Definição da Paginação (page - 1 porque as páginas são base 0 no Spring Data)
        PageRequest pageable = PageRequest.of(query.getPage() - 1, query.getLimit());
        List<PhysicianReadModel> results;

        // 2. Lógica de Busca no MongoDB
        if (query.getName() != null) {
            // NOTE: Este método precisa ser implementado no PhysicianReadRepository (MongoDB)
            // Exemplo: return physicianReadRepository.findByNameContainingIgnoreCase(query.getName(), pageable).getContent();
            results = physicianReadRepository.findAll(pageable).getContent();

        } else if (query.getSpecialty() != null) {
            // NOTE: Este método precisa ser implementado no PhysicianReadRepository (MongoDB)
            // Exemplo: return physicianReadRepository.findBySpecialty(query.getSpecialty(), pageable).getContent();
            results = physicianReadRepository.findAll(pageable).getContent();

        } else {
            // Busca sem filtros, apenas paginação de todos os médicos
            results = physicianReadRepository.findAll(pageable).getContent();
        }

        // 3. Mapeamento para DTO
        return results.stream()
                .map(this::mapToPhysicianDTO)
                .collect(Collectors.toList());
    }

    private PhysicianDTO mapToPhysicianDTO(PhysicianReadModel model) {
        // Mapeamento idêntico ao usado nas outras Queries
        return new PhysicianDTO(
                model.getId(),
                model.getPhysicianNumber(),
                model.getName(),
                model.getSpecialty()
        );
    }
}
