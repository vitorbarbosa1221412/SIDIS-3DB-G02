package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.infrastructure;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model.AppointmentRecord;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.repositories.AppointmentRecordRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.exceptions.NotFoundException;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {"appointmentRecords"})
public interface SpringDataAppointmentRecordRepository extends AppointmentRecordRepository, AppointmentRecordRepoCustom, CrudRepository<AppointmentRecord, Long> {

    @CacheEvict(allEntries = true)
    <S extends AppointmentRecord> List<S> saveAll(Iterable<S> entities);

    @CacheEvict(allEntries = true)
    <S extends AppointmentRecord> S save(S entity);

    @Cacheable
    List<AppointmentRecord> findAll();

    @Cacheable
    Optional<AppointmentRecord> findById(Long id);

    @Cacheable
    default AppointmentRecord getByIdChecked(Long id) {
        return this.findById(id)
                .orElseThrow(() -> new NotFoundException(AppointmentRecord.class, id.toString()));
    }

    @Cacheable
    Optional<AppointmentRecord> findByAppointment(Appointment appointment);

    Optional<AppointmentRecord> findByRecordNumber(Long recordNumber);

    @Cacheable
    List<AppointmentRecord> findByAppointment_PatientId(String patientNumber);
    List<AppointmentRecord> getAppointmentRecordByRecordNumber(Long recordNumber);

//    @Query("SELECT ar FROM AppointmentRecord ar WHERE ar.appointment.patientId = :patient")
//    List<AppointmentRecord> getAppointmentRecordByPatient(String patientId);

    @Query("SELECT ar.appointment.patientId, " +
            "ar.appointment.physicianNumber, " +
            "ar.prescription " +
            "FROM AppointmentRecord ar " +
            "WHERE ar.recordNumber = :recordNumber")
    List<Object[]> findElectronicPrescriptionParameters(Long recordNumber);
}
