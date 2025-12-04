package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
// PhysicianWriteRepository.java
public interface PhysicianWriteRepository extends JpaRepository<Physician, Long> {
    Optional<Physician> findByPhysicianNumber(String physicianNumber);
}