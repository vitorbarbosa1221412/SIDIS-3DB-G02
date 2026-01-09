package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.infrastructure;


import java.io.Serializable;

/**
 * Interface para publicação de eventos de domínio no Message Broker.
 */
public interface EventPublisher {

    /**
     * Publica um evento no Message Broker.
     *
     * @param exchange O nome do ponto de troca (Exchange) do RabbitMQ.
     * @param routingKey A chave de roteamento.
     * @param event O objeto de evento (deve ser Serializable).
     */
    void publish(String exchange, String routingKey, Serializable event);
}
