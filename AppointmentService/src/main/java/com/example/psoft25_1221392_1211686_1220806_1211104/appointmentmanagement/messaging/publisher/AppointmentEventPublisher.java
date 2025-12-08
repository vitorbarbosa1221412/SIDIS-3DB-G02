package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.publisher;

import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.config.RabbitMQConfig;
import com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.dto.AppointmentRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAppointmentRequestedEvent(AppointmentRequestedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.APPOINTMENT_EXCHANGE,
                RabbitMQConfig.APPOINTMENT_REQUESTED_ROUTING_KEY,
                event
            );
            log.info("Published AppointmentRequestedEvent for Appointment: {}", event.getPatientId());
        } catch (Exception e) {
            log.error("Failed to publish AppointmentRequestedEvent: {}", e.getMessage(), e);
        }
    }
}

