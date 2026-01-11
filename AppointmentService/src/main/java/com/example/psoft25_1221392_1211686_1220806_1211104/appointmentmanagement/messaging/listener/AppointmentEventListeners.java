package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.listener;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.config.RabbitMQConfig;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.dto.PatientBookedFailEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.dto.PhysicianBookedFailEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.Appointment;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.model.AppointmentStatus;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.config.RabbitMQConfig.PATIENTS_EVENTS_QUEUE;
import static com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.config.RabbitMQConfig.PHYSICIAN_EVENTS_QUEUE;

/**
 * Event Listeners for external service events
 * Demonstrates Choreography pattern - services react to events independently
 * This service listens to events from Appointment and Physician services
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventListeners {

    @Autowired
    private AppointmentRepository appointmentRepository;

    /**
     * Listen to appointment events (e.g., when appointment is created/updated/cancelled)
     * This allows Patient Service to react to appointment changes asynchronously
     * Example: Update patient's appointment history cache, send notifications, etc.
     */
    @RabbitListener(queues = RabbitMQConfig.APPOINTMENT_EVENTS_QUEUE)
    public void handleAppointmentEvent(Object event) {
        log.info("Received appointment event: {}", event);
        // Process appointment event
        // Example: Update patient's appointment count, cache appointment data, etc.
        // This demonstrates choreography - Patient Service reacts independently
    }

    @RabbitListener(queues = PHYSICIAN_EVENTS_QUEUE)
    public void onPhysicianBookingFailed(PhysicianBookedFailEvent event) {

        Appointment appointment = appointmentRepository
                .findById(event.getAppointmentNumber())
                .orElseThrow();

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        log.warn("Appointment {} cancelled due to physician failure",
                appointment.getAppointmentNumber());
    }

    @RabbitListener(queues = PATIENTS_EVENTS_QUEUE)
    public void onPatientBookingFailed(PatientBookedFailEvent event) {

        Appointment appointment = appointmentRepository
                .findById(event.getAppointmentNumber())
                .orElseThrow();

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        log.warn("Appointment {} cancelled due to physician failure",
                appointment.getAppointmentNumber());
    }


    /**
     * Listen to physician events (e.g., when physician availability changes)
     * This allows Patient Service to react to physician changes asynchronously
     */
    @RabbitListener(queues = PHYSICIAN_EVENTS_QUEUE)
    public void handlePhysicianEvent(Object event) {
        log.info("Received physician event: {}", event);
        // Process physician event if needed
        // Example: Update physician availability cache, notify patients, etc.
    }
}

