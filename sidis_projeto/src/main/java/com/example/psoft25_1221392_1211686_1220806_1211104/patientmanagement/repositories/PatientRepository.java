package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.repositories;

import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface PatientRepository {

    <S extends Patient> List<S> saveAll(Iterable<S> entities);

    List<Patient> findAll();

    <S extends Patient> S save(S entity);

    Optional<Patient> findById(Long objectId);
    Optional<Patient> findByUser_Id(Long userId);

    default Patient getById(final Long id) {
        final Optional<Patient> maybePatient = findById(id);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybePatient.filter(Patient::isEnabled).orElseThrow(() -> new NotFoundException(Patient.class, id));
    }

    Optional<Patient> findByPatientNumber(String patientNumber);

    default Patient getByPatientNumber(final String patientNumber) {
        final Optional<Patient> maybePatient = findByPatientNumber(patientNumber);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybePatient.filter(Patient::isEnabled).orElseThrow(() -> new NotFoundException(Patient.class, patientNumber));
    }

    Optional<Patient> findByEmailAddress(String emailAddress);

    default Patient getByEmailAddress(final String emailAddress) {
        final Optional<Patient> maybePatient = findByEmailAddress(emailAddress);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybePatient.filter(Patient::isEnabled).orElseThrow(() -> new NotFoundException(Patient.class, emailAddress));
    }

    List<Patient> findByName(String name);

    default String getLastPatientNumber(final String year){
        final String lastPatientNumber = findLastPatientNumber(year);

        return Objects.requireNonNullElseGet(lastPatientNumber, () -> year + "/0");
    }

    String findLastPatientNumber(final String year);

    Optional<Patient> findByUserUsername(String username);


    Optional<Object> findByUserId(Long userId);

}
