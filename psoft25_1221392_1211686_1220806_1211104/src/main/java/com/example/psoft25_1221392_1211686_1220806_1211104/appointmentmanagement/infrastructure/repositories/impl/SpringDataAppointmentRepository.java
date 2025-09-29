package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.infrastructure.repositories.impl;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.api.PhysicianAverageDurationDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;

@Repository
public interface SpringDataAppointmentRepository
        extends JpaRepository<Appointment, Long>,
        AppointmentRepoCustom {

    List<Appointment> findByPhysicianId(Long physId);

    List<Appointment> findByPatientId(Long patientId);

    @Query("""
    SELECT a FROM Appointment a
    WHERE a.physician.physicianNumber = :physicianNumber
    AND a.dateTime BETWEEN :start AND :end
""")
    List<Appointment> findAppointmentsByPhysicianNumberAndDate(
            @Param("physicianNumber") String physicianNumber,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("SELECT a.physician FROM Appointment a GROUP BY a.physician ORDER BY COUNT(a) DESC")
    List<Physician> getTop5Physicians(Pageable pageable);

    @Query("""
    SELECT a FROM Appointment a
    WHERE a.dateTime > :now
    ORDER BY a.dateTime ASC
""")
    List<Appointment> findUpcomingAppointments(@Param("now") LocalDateTime now);

    @Query(value = """
    SELECT p.name AS physician_name,
           AVG(DATEDIFF('MINUTE', a.start_time, a.end_time)) AS avg_duration_minutes
    FROM appointment a
    JOIN physician p ON a.physician_id = p.id
    WHERE a.start_time IS NOT NULL AND a.end_time IS NOT NULL
    GROUP BY p.name
""", nativeQuery = true)
    List<Object[]> findAverageAppointmentDurationPerPhysician();

}
