package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.infrastructure.repositories.impl;

import java.util.List;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.AgeGroupStats;

public interface AppointmentRepoCustom {
    /** agendar um novo compromisso */
    Appointment saveCustom(Appointment appt);

    /** buscar pelo campo “appointmentNumber” */
    Appointment findByAppointmentNumber(String number);

    /** listar todos os compromissos futuros (não cancelados) de um dado paciente */
    List<Appointment> findUpcomingForPatient(Long patientId);

    /** cancelar um compromisso existente */
    void cancelAppointment(Long appointmentId);


}
