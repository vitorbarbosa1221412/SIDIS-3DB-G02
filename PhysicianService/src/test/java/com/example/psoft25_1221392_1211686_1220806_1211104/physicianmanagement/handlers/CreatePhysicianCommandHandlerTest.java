package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.handlers;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.CreatePhysicianCommand;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianWriteRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.infrastructure.EventPublisher;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@ExtendWith(MockitoExtension.class)
public class CreatePhysicianCommandHandlerTest {

    // Simula o repositório PostgreSQL
    @Mock
    private PhysicianWriteRepository physicianWriteRepository;

    // Simula o serviço RabbitMQ
    @Mock
    private EventPublisher eventPublisher;

    // Injeta os mocks no handler que queremos testar
    @InjectMocks
    private CreatePhysicianCommandHandler handler;

    @Test
    void shouldSavePhysicianAndPublishEvent() {

        CreatePhysicianCommand command = mock(CreatePhysicianCommand.class);


        Physician savedPhysician = new Physician();
        savedPhysician.setId(1L);
        savedPhysician.setPhysicianNumber("P-001");

        when(physicianWriteRepository.save(any(Physician.class))).thenReturn(savedPhysician);




        assertDoesNotThrow(() -> handler.handle(command));




        verify(physicianWriteRepository, times(1)).save(any(Physician.class));


        verify(eventPublisher, times(1)).publish(
                eq("physician_events"), // Verifica o nome da Exchange
                eq("physician.created"), // Verifica a Routing Key
                any() // Verifica se qualquer objeto Serializable (PhysicianCreatedEvent) foi enviado
        );
    }


}
