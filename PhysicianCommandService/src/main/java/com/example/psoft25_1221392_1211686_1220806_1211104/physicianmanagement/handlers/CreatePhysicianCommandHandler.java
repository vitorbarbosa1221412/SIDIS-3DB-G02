package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.handlers;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.CreatePhysicianCommand;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.events.PhysicianCreatedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.PhysicianAggregate;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.StoredEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.EventStoreRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.infrastructure.EventPublisher;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.services.UserService;

// IMPORTS NOVOS OBRIGATÓRIOS PARA CORRIGIR O ERRO 500
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class CreatePhysicianCommandHandler extends BaseCommandHandler<CreatePhysicianCommand> {

    // Simulação de gerador de ID (PostgreSQL sequence/UUID em produção)
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Repositório que salva/carrega eventos no PostgreSQL (Event Store)
    private final EventStoreRepository eventStoreRepository;

    // Outras injeções mantidas
    private final UserService userService;
    private final EventPublisher eventPublisher;

    @Autowired
    public CreatePhysicianCommandHandler(
            EventStoreRepository eventStoreRepository,
            UserService userService,
            EventPublisher eventPublisher)
    {
        this.eventStoreRepository = eventStoreRepository;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void handle(CreatePhysicianCommand command) {

        // 1. GERAÇÃO DE EVENTOS
        Long newAggregateId = ID_GENERATOR.getAndIncrement();
        int nextVersion = 1;

        // O Agregado gera os eventos necessários para a sua criação
        List<Object> generatedEvents = PhysicianAggregate.create(newAggregateId, command);

        // 2. PERSISTÊNCIA E PUBLICAÇÃO (LOOP DE EVENTOS)
        generatedEvents.forEach(eventObject -> {
            try {
                if (eventObject instanceof PhysicianCreatedEvent event) {

                    // Serializa o evento para JSONB (AGORA VAI FUNCIONAR!)
                    String eventData = OBJECT_MAPPER.writeValueAsString(event);

                    // Armazena no Event Store (PostgreSQL)
                    StoredEvent storedEvent = new StoredEvent(
                            newAggregateId,
                            event.getClass().getSimpleName(),
                            eventData,
                            nextVersion
                    );
                    eventStoreRepository.save(storedEvent);

                    // 3. Publicar Evento para o Read Side (RabbitMQ)
                    eventPublisher.publish("physician.events", "physician.created", event);
                }
            } catch (Exception e) {
                // Em caso de falha, faz rollback e mostra o erro original
                throw new RuntimeException("Falha no Event Sourcing ao persistir ou publicar evento.", e);
            }
        });
    }
}
