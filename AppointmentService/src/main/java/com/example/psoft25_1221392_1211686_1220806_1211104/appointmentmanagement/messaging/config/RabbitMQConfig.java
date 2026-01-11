package com.example.psoft25_1221392_1211686_1220806_1211104.appointmentmanagement.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 * Sets up exchanges, queues, and bindings for event-driven communication
 */
@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String PATIENT_EXCHANGE = "patient.exchange";
    public static final String APPOINTMENT_EXCHANGE = "appointment.exchange";
    public static final String PHYSICIAN_EXCHANGE = "physician.exchange";

    // Queue names for patient events
    public static final String PATIENT_CREATED_QUEUE = "patient.created.queue";
    public static final String PATIENT_UPDATED_QUEUE = "patient.updated.queue";
    public static final String PATIENT_DELETED_QUEUE = "patient.deleted.queue";
    public static final String APPOINTMENT_REQUESTED_QUEUE = "appointment.requested.queue";

    // Queue names for listening to other services
    public static final String APPOINTMENT_EVENTS_QUEUE = "appointment.events.patient.queue";
    public static final String PHYSICIAN_EVENTS_QUEUE = "physician.events.patient.queue";
    public static final String PATIENTS_EVENTS_QUEUE = "patients.events.patient.queue";

    // Routing keys
    public static final String PATIENT_CREATED_ROUTING_KEY = "patient.created";
    public static final String PATIENT_UPDATED_ROUTING_KEY = "patient.updated";
    public static final String PATIENT_DELETED_ROUTING_KEY = "patient.deleted";
    public static final String APPOINTMENT_REQUESTED_ROUTING_KEY = "appointment.requested";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    // Patient Exchange and Queues
    @Bean
    public TopicExchange patientExchange() {
        return new TopicExchange(PATIENT_EXCHANGE);
    }

    @Bean
    public Queue patientCreatedQueue() {
        return QueueBuilder.durable(PATIENT_CREATED_QUEUE).build();
    }

    @Bean
    public Queue patientUpdatedQueue() {
        return QueueBuilder.durable(PATIENT_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue patientDeletedQueue() {
        return QueueBuilder.durable(PATIENT_DELETED_QUEUE).build();
    }

    @Bean
    public Queue appointmentRequestedQueue() {
        return QueueBuilder.durable(APPOINTMENT_REQUESTED_QUEUE).build();
    }

    @Bean
    public Binding patientCreatedBinding() {
        return BindingBuilder
            .bind(patientCreatedQueue())
            .to(patientExchange())
            .with(PATIENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding patientUpdatedBinding() {
        return BindingBuilder
            .bind(patientUpdatedQueue())
            .to(patientExchange())
            .with(PATIENT_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding patientDeletedBinding() {
        return BindingBuilder
            .bind(patientDeletedQueue())
            .to(patientExchange())
            .with(PATIENT_DELETED_ROUTING_KEY);
    }

    @Bean
    public Binding appointmentRequestedBinding() {
        return BindingBuilder
                .bind(appointmentRequestedQueue())
                .to(patientExchange())
                .with(APPOINTMENT_REQUESTED_ROUTING_KEY);
    }

    // External Service Event Queues (for listening to other services - Choreography pattern)
    @Bean
    public TopicExchange appointmentExchange() {
        return new TopicExchange(APPOINTMENT_EXCHANGE);
    }

    @Bean
    public TopicExchange physicianExchange() {
        return new TopicExchange(PHYSICIAN_EXCHANGE);
    }

    @Bean
    public Queue appointmentEventsQueue() {
        return QueueBuilder.durable(APPOINTMENT_EVENTS_QUEUE).build();
    }

    @Bean
    public Queue physicianEventsQueue() {
        return QueueBuilder.durable(PHYSICIAN_EVENTS_QUEUE).build();
    }

    @Bean
    public Binding appointmentEventsBinding() {
        return BindingBuilder
            .bind(appointmentEventsQueue())
            .to(appointmentExchange())
            .with("appointment.*");
    }

    @Bean
    public Binding physicianEventsBinding() {
        return BindingBuilder
            .bind(physicianEventsQueue())
            .to(physicianExchange())
            .with("physician.*");
    }
}

