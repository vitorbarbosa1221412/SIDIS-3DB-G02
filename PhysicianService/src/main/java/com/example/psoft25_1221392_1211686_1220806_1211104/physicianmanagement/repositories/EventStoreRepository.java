package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.StoredEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventStoreRepository extends JpaRepository<StoredEvent, Long> {


    List<StoredEvent> findByAggregateIdOrderByVersionAsc(Long aggregateId);

    long countByAggregateId(Long aggregateId);
}