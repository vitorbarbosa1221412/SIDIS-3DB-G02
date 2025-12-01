package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.publisher;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.config.RabbitMQConfig;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.PatientCreatedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.PatientUpdatedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.PatientDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Event Publisher for Patient events
 * Publishes events to RabbitMQ for async communication (CQRS pattern)
 * This enables choreography - other services can react to patient events independently
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PatientEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishPatientCreated(PatientCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.PATIENT_EXCHANGE,
                RabbitMQConfig.PATIENT_CREATED_ROUTING_KEY,
                event
            );
            log.info("Published PatientCreatedEvent for patient: {}", event.getPatientNumber());
        } catch (Exception e) {
            log.error("Failed to publish PatientCreatedEvent: {}", e.getMessage(), e);
            // In production, consider using an outbox pattern for guaranteed delivery
        }
    }

    public void publishPatientUpdated(PatientUpdatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.PATIENT_EXCHANGE,
                RabbitMQConfig.PATIENT_UPDATED_ROUTING_KEY,
                event
            );
            log.info("Published PatientUpdatedEvent for patient: {}", event.getPatientNumber());
        } catch (Exception e) {
            log.error("Failed to publish PatientUpdatedEvent: {}", e.getMessage(), e);
        }
    }

    public void publishPatientDeleted(PatientDeletedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.PATIENT_EXCHANGE,
                RabbitMQConfig.PATIENT_DELETED_ROUTING_KEY,
                event
            );
            log.info("Published PatientDeletedEvent for patient: {}", event.getPatientNumber());
        } catch (Exception e) {
            log.error("Failed to publish PatientDeletedEvent: {}", e.getMessage(), e);
        }
    }
}

