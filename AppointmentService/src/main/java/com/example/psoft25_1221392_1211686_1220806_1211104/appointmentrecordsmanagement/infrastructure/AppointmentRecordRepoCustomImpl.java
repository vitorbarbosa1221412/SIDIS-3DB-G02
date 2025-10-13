package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.infrastructure;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.model.AppointmentRecord;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentrecordsmanagement.services.Page;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository

class AppointmentRecordRepoCustomImpl implements AppointmentRecordRepoCustom {


    @PersistenceContext
    private EntityManager em;

    @Override
    public List<AppointmentRecord> getAppointmentRecordByPatientNumber(Page page, Long patientNumber) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AppointmentRecord> cq = cb.createQuery(AppointmentRecord.class);
        Root<AppointmentRecord> root = cq.from(AppointmentRecord.class);

        // Join: AppointmentRecord -> Appointment -> Patient
        Join<Object, Object> appointmentJoin = root.join("appointment");
        Join<Object, Object> patientJoin = appointmentJoin.join("patient");

        cq.select(root).where(cb.equal(patientJoin.get("patientNumber"), patientNumber));

        TypedQuery<AppointmentRecord> query = em.createQuery(cq);
        query.setFirstResult((page.getNumber() - 1) * page.getLimit());
        query.setMaxResults(page.getLimit());

        return query.getResultList();
    }

    @Override
    public List<AppointmentRecord> getAppointmentRecordByRecordNumber(Page page, Long recordNumber) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AppointmentRecord> cq = cb.createQuery(AppointmentRecord.class);
        Root<AppointmentRecord> root = cq.from(AppointmentRecord.class);

        cq.select(root).where(cb.equal(root.get("id"), recordNumber));

        TypedQuery<AppointmentRecord> query = em.createQuery(cq);
        query.setFirstResult((page.getNumber() - 1) * page.getLimit());
        query.setMaxResults(page.getLimit());

        return query.getResultList();
    }
}
