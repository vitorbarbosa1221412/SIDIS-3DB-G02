package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.repositories;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model.AppointmentRecord;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AppointmentRecordRepository extends JpaRepository <AppointmentRecord, Long> {

    <S extends AppointmentRecord> S save(S entity);

    <S extends AppointmentRecord> List<S> saveAll(Iterable<S> entities);

    List<AppointmentRecord> findAll();

    Optional<AppointmentRecord> findById(Long id);

    Optional<AppointmentRecord> findByAppointment(Appointment appointment);

    Optional<AppointmentRecord> findByRecordNumber(Long recordNumber);

//    List<AppointmentRecord> findByAppointment_PatientId(String patientId);

    @Query("SELECT MAX(ar.recordNumber) FROM AppointmentRecord ar WHERE FUNCTION('YEAR', ar.appointment) = :year")
    Long getLastRecordNumber(@Param("year") int year);

    List<AppointmentRecord> findByAppointment_PatientId(String patientNumber);

    List<AppointmentRecord> getAppointmentRecordByRecordNumber(Long recordNumber);

    @Query("SELECT ar FROM AppointmentRecord ar WHERE ar.appointment.patientId = :patientNumber AND ar.recordNumber = :recordNumber")
    List<AppointmentRecord> searchByPatientIdAndRecordNumber(String patientNumber, Long recordNumber);

    @Query("SELECT ar.appointment.patientId, " +
            "ar.appointment.physicianNumber, " +
            "ar.prescription " +
            "FROM AppointmentRecord ar " +
            "WHERE ar.recordNumber = :recordNumber")
    List<Object[]> findElectronicPrescriptionParameters(Long recordNumber);

    default AppointmentRecord getByIdChecked(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException(AppointmentRecord.class, id.toString()));
    }

    boolean existsByAppointment(Appointment appointment);

        @Query("SELECT MAX(a.recordNumber) FROM AppointmentRecord a")
        Optional<Long> findMaxRecordNumber();



}
