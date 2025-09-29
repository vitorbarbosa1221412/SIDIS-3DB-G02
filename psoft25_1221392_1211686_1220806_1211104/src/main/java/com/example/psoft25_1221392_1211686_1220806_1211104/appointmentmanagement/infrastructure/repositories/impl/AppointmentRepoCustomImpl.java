package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.infrastructure.repositories.impl;


import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.AgeGroupStats;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.infrastructure.repositories.impl.AppointmentRepoCustom;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.AppointmentStatus;

@Repository
@Transactional(readOnly = true)
public class AppointmentRepoCustomImpl implements AppointmentRepoCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Appointment saveCustom(Appointment appt) {
        // faz persist ou merge conforme o fluxo
        if (appt.getAppointmentNumber() == null) {
            em.persist(appt);
            return appt;
        } else {
            return em.merge(appt);
        }
    }

    @Override
    public Appointment findByAppointmentNumber(String number) {
        TypedQuery<Appointment> q = em.createQuery(
                "SELECT a FROM Appointment a WHERE a.appointmentNumber = :num",
                Appointment.class);
        q.setParameter("num", number);
        return q.getSingleResult();
    }

    @Override
    public List<Appointment> findUpcomingForPatient(Long patientId) {
        TypedQuery<Appointment> q = em.createQuery(
                "SELECT a FROM Appointment a "
                        + "WHERE a.patient.id = :pid "
                        + "  AND a.status <> :cancelled "
                        + "ORDER BY a.dateTime ASC",
                Appointment.class);
        q.setParameter("pid", patientId);
        q.setParameter("cancelled", AppointmentStatus.CANCELLED);
        return q.getResultList();
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId) {
        Appointment a = em.find(Appointment.class, appointmentId);
        if (a != null) {
            a.setStatus(AppointmentStatus.CANCELLED);
            em.merge(a);
        }
    }



}
