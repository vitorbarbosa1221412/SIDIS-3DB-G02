package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.infrastructure;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;

/**
 * Implementação do EventPublisher usando o RabbitMQ.
 * Atua como o canal de comunicação para a Projeção CQRS e comunicação inter-serviços.
 */
@Service
public class RabbitEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(String exchange, String routingKey, Serializable event) {
        try {
            // Converte o objeto Java (PhysicianCreatedEvent, etc.) e envia.
            // O Spring Boot AMQP lida com a serialização (geralmente JSON, dependendo da sua config).
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            System.out.println("EVENT PUBLISHED: " + event.getClass().getSimpleName() +
                    " to Exchange: " + exchange + ", Routing Key: " + routingKey);
        } catch (Exception e) {
            // Lógica de tratamento de falhas na publicação (logging, retries, Outbox Pattern, etc.)
            System.err.println("Failed to publish event: " + e.getMessage());
            // Dependendo dos requisitos, você pode relançar a exceção ou apenas logar.
            throw new RuntimeException("Messaging error during event publication.", e);
        }
    }
}