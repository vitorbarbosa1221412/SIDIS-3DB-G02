package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.listener;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.config.RabbitMQConfig;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.AppointmentRequestedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.PatientBookedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.PatientBookedFailEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.publisher.PatientEventPublisher;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientSagaListener {

    private final PatientService patientService;
    private final PatientEventPublisher publisher;

    @RabbitListener(queues = RabbitMQConfig.APPOINTMENT_EVENTS_QUEUE)
    public void handleAppointmentRequested(AppointmentRequestedEvent event) {
        try {
            // validate patient
            publisher.publishPatientBookedEvent(
                    PatientBookedEvent.builder()
                            .appointmentNumber(event.getAppointmentNumber())
                            .patientId(event.getPatientId())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        } catch (Exception ex) {
            publisher.publishPatientBookedFailEvent(
                    PatientBookedFailEvent.builder()
                            .appointmentNumber(event.getAppointmentNumber())
                            .reason(ex.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }
}