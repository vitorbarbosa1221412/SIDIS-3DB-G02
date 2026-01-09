package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories;

import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services.Page;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhysicianRepository extends JpaRepository<Physician, Long> {
    <S extends Physician> List<S> saveAll(Iterable<S> entities);

    List<Physician> findAll();

    <S extends Physician> S save(S entity);

    Optional<Physician> findById(Long objectId);

    List<Physician> findBySpecialtyContaining(String specialty);

    default Physician getByPhysicianNumber(final String physicianNumber){
        final Optional<Physician> maybePhysician = findByPhysicianNumber(physicianNumber);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybePhysician.filter(Physician::isEnabled).orElseThrow(() -> new NotFoundException(Physician.class, physicianNumber));
    }
    List<Physician> findByNameContaining(String name);

    @Query("SELECT MAX(p.physicianNumber) FROM Physician p WHERE p.physicianNumber LIKE CONCAT('PH-', :year, '-%')")
    String findLastPhysicianNumber(@Param("year") String year);



    Optional<Physician> findByPhysicianNumber(String physicianNumber);

    default String getLastPhysicianNumber(final String year) {
        final String last = findLastPhysicianNumber(year);

        if (last == null || last.isBlank() || !last.startsWith("PH-")) {
            return "PH-" + year + "-0";
        }

        return last;
    }



}

