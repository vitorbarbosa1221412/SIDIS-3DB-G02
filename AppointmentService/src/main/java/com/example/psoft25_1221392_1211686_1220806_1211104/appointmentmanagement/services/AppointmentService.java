package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.api.PhysicianAverageDurationDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.ConsultationType;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {

    // Agenda um novo compromisso
    Appointment createAppointment(CreateAppointmentRequest request);

    // Atualiza (ou cancela) um compromisso existente
    Appointment updateAppointment(String appointmentNumber, UpdateAppointmentRequest request);

    //Retorna o compromisso pelo número único
    ResponseEntity<Appointment> viewAppointmentByNumber(String appointmentNumber);

    // Lista todos os compromissos (poderia filtrar depois por status, data, etc)
    List<Appointment> getAllAppointments();

    //  Cancela um compromisso já agendado
    void cancelByAppointmentNumber(String appointmentNumber);

    List<LocalTime> getAvailableSlots(String physicianNumber, LocalDate date);

    Appointment scheduleAppointmentByPatient(String patientId, String physicianId, LocalDateTime dateTime, ConsultationType type);

//    List<AgeGroupStats> getAppointmentStatsByAgeGroup();


    List<Appointment> getAppointmentHistory(String userId);

    //Lista as próximas consultas
    List<Appointment> getUpcomingAppointments();

//    List<PhysicianAverageDurationDTO> getAverageAppointmentDurationPerPhysician();

    List<AppointmentServiceImpl.MonthlyAppointmentReport> getMonthlyReport();

}