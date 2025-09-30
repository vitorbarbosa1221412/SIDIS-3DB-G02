package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.infrastructure.repositories.impl;

import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.Page;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.repositories.PatientRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@CacheConfig(cacheNames = "patients")
@Primary
public interface SpringDataPatientRepository extends PatientRepository, PatientRepoCustom, CrudRepository<Patient,Long> {

    @Override
    @CacheEvict(allEntries = true)
    <S extends Patient> List<S> saveAll(Iterable<S> entities);

    <S extends Patient> S save(S entity);

    /**
     * findById searches a specific user and returns an optional
     */
    @Override
    @Cacheable
    Optional<Patient> findById(Long objectId);

    /**
     * getById explicitly loads a user or throws an exception if the user does not
     * exist or the account is not enabled
     *
     * @param id
     * @return
     */
    @Cacheable
    default Patient getById(final Long id) {
        final Optional<Patient> maybePatient = findById(id);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybePatient.filter(Patient::isEnabled).orElseThrow(() -> new NotFoundException(Patient.class, id));
    }

    @Cacheable
    Optional<Patient> findByEmailAddress(String emailAddress);

    @Cacheable
    default Patient getByEmailAddress(final String emailAddress) {
        final Optional<Patient> maybePatient =  findByEmailAddress(emailAddress);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybePatient.filter(Patient::isEnabled).orElseThrow(() -> new NotFoundException(Patient.class, emailAddress));
    }

    @Cacheable
    Optional<Patient> findByPatientNumber(String patientNumber);

    @Cacheable
    default Patient getByPatientNumber(final String patientNumber) {
        final Optional<Patient> maybePatient = findByPatientNumber(patientNumber);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybePatient.filter(Patient::isEnabled).orElseThrow(() -> new NotFoundException(Patient.class, patientNumber));
    }

    @Override
    @Query("SELECT p.patientNumber FROM Patient p WHERE p.patientNumber LIKE %:year% ORDER BY p.id DESC LIMIT 1")
    String findLastPatientNumber(final String year);

    @Cacheable
    default String getLastPatientNumber(final String year){
        final String lastPatientNumber = findLastPatientNumber(year);

        return Objects.requireNonNullElseGet(lastPatientNumber, () -> year + "/0");
    }
}

interface PatientRepoCustom {

    List<Patient> findByName(Page page, String name);
}



@RequiredArgsConstructor
class PatientRepoCustomImpl implements PatientRepoCustom {

    // get the underlying JPA Entity Manager via spring thru constructor dependency
    // injection
    private final EntityManager em;

    @Override
    public List<Patient> findByName(final Page page, String name) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Patient> cq = cb.createQuery(Patient.class);
        final Root<Patient> root = cq.from(Patient.class);
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();
        if (StringUtils.hasText(name)) {
            where.add(cb.like(root.get("name"), "%"+name+"%"));
        }

        // search using OR
        if (!where.isEmpty()) {
            cq.where(cb.or(where.toArray(new Predicate[0])));
        }

        final TypedQuery<Patient> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        return q.getResultList();
    }
}
