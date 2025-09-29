package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.repositories;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.AppointmentNumber;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.AppointmentStatus;
import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.PhysicianNumber;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    <S extends Appointment> S save(S entity);

    <S extends Appointment> List<S> saveAll(Iterable<S> entities);

    List<Appointment> findAll();

    Optional<Appointment> findByAppointmentNumber(AppointmentNumber number);

    List<Appointment> findByPatient(Patient patient, Pageable page);

    List<Appointment> findByPhysician(Physician physician, Pageable page);

    List<Appointment> findByStatus(AppointmentStatus status, Pageable page);

    default Appointment getByAppointmentNumberChecked(String appointmentNumber) {
        return findByAppointmentNumber(new AppointmentNumber(appointmentNumber))

                .orElseThrow(() -> new NotFoundException(Appointment.class, appointmentNumber));
    }
    Optional<Appointment> findByPatientAndPhysicianAndDateTime(
            Patient patient,
            Physician physician,
            LocalDateTime dateTime);
    @Query("SELECT a.physician FROM Appointment a GROUP BY a.physician ORDER BY COUNT(a) DESC")
    List<Physician> getTop5Physicians(Pageable pageable);

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

    @Query("SELECT MAX(CAST(SUBSTRING(a.appointmentNumber.value, 6) AS int)) FROM Appointment a")
    Integer findMaxAppointmentNumber();

    List<Appointment> findByPatient_PatientNumberOrderByDateTimeDesc(String patientNumber);

    @Query("""
    SELECT a FROM Appointment a
    WHERE a.dateTime > :now
    ORDER BY a.dateTime ASC
""")
    List<Appointment> findUpcomingAppointments(@Param("now") LocalDateTime now);

    @Query(value = """
    SELECT p.name AS physician_name,
           AVG(DATEDIFF('SECOND', a.start_time, a.end_time)) AS avg_duration_seconds
    FROM appointment a
    JOIN physician p ON a.physician_id = p.id
    WHERE a.start_time IS NOT NULL AND a.end_time IS NOT NULL
    GROUP BY p.name
""", nativeQuery = true)
    List<Object[]> findAverageAppointmentDurationPerPhysician();

    Optional<Appointment> findByAppointmentNumber_Value(String appointmentNumber);

}
