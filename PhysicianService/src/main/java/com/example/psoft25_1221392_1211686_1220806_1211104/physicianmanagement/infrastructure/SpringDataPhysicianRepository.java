package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.infrastructure;

import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services.Page;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Primary
public interface SpringDataPhysicianRepository extends PhysicianRepository, PhysicianRepoCustom, CrudRepository<Physician,Long> {
    @Override
    @CacheEvict(allEntries = true)
    <S extends Physician> List<S> saveAll(Iterable<S> entities);

    <S extends Physician> S save(S entity);

    @Cacheable
    List<Physician> findBySpecialty(String specialty);

    @Cacheable
    default Physician getBySpecialty(final String specialty) {
        final List<Physician> maybePhysician = findBySpecialty(specialty);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybePhysician.stream()
                .filter(Physician::isEnabled)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Physician not found or not enabled"));    }

    Optional<Physician> findByPhysicianNumber(String physicianNumber);

    @Cacheable
    default Physician getByPhysicianNumber(final String physicianNumber) {
        final Optional<Physician> maybePhysician = findByPhysicianNumber(physicianNumber);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybePhysician.filter(Physician::isEnabled).orElseThrow(() -> new NotFoundException(Physician.class, physicianNumber));
    }

    @Override
    @Query("SELECT r.physicianNumber FROM Physician r WHERE r.physicianNumber LIKE %:year% ORDER BY r.id DESC LIMIT 1")
    String findLastPhysicianNumber(final String year);

    @Cacheable
    default String getLastPhysicianNumber(final String year){
        final String lastPhysicianNumber = findLastPhysicianNumber(year);

        if(lastPhysicianNumber == null)
        {
            return year+"-0";
        }else
        {
            return lastPhysicianNumber;
        }
    }


}

interface PhysicianRepoCustom {

    List<Physician> getPhysicianByName(Page page, String name);
}

@RequiredArgsConstructor
class PhysicianRepoCustomImpl implements PhysicianRepoCustom {

    // get the underlying JPA Entity Manager via spring thru constructor dependency
    // injection
    private final EntityManager em;

    @Override
    public List<Physician> getPhysicianByName(final Page page, String name) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Physician> cq = cb.createQuery(Physician.class);
        final Root<Physician> root = cq.from(Physician.class);
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();
        if (StringUtils.hasText(name)) {
            where.add(cb.like(root.get("name"), "%"+name+"%"));
        }

        // search using OR
        if (!where.isEmpty()) {
            cq.where(cb.or(where.toArray(new Predicate[0])));
        }

        final TypedQuery<Physician> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        return q.getResultList();
    }
}



