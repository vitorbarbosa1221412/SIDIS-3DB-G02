package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.readmodels.PhysicianReadModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório de Leitura (Read Repository) para o CQRS.
 * Lida com o acesso de dados no MongoDB (Read DB).
 */
@Repository // 👈 ANOTAÇÃO CRUCIAL
public interface PhysicianReadRepository extends MongoRepository<PhysicianReadModel, Long> {

    // Método de busca necessário para o GetPhysicianByNumberQueryHandler
    Optional<PhysicianReadModel> findByPhysicianNumber(String physicianNumber);
}
