package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhysicianRepository extends MongoRepository<Physician, Long> {

    // Métodos de pesquisa específicos para o Mongo
    List<Physician> findBySpecialtyContaining(String specialty);

    List<Physician> findByNameContaining(String name);

    Optional<Physician> findByPhysicianNumber(String physicianNumber);
}

