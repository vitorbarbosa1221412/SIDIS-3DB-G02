package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.handlers;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.UpdatePhysicianCommand;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.events.PhysicianUpdatedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianWriteRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.infrastructure.EventPublisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Component
public class UpdatePhysicianCommandHandler extends BaseCommandHandler<UpdatePhysicianCommand> {

    private final PhysicianWriteRepository physicianWriteRepository;
    private final EventPublisher eventPublisher;

    @Autowired
    public UpdatePhysicianCommandHandler(
            PhysicianWriteRepository physicianWriteRepository,
            EventPublisher eventPublisher)
    {
        this.physicianWriteRepository = physicianWriteRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void handle(UpdatePhysicianCommand command) {

        // 1. EXECUÇÃO DA LÓGICA (WRITE DB - POSTGRESQL)
        Physician physician = physicianWriteRepository.findByPhysicianNumber(command.getPhysicianNumber())
                .orElseThrow(() -> new NoSuchElementException("Physician not found with number: " + command.getPhysicianNumber()));

        updatePhysicianFromRequest(physician, command);
        Physician updatedPhysician = physicianWriteRepository.save(physician);

        // 2. PUBLICAR EVENTO PARA PROJEÇÃO (RABBITMQ)
        PhysicianUpdatedEvent event = new PhysicianUpdatedEvent(
                updatedPhysician.getId(),
                updatedPhysician.getPhysicianNumber(),
                updatedPhysician.getName(),
                updatedPhysician.getSpecialty()
        );
        eventPublisher.publish("physician.events", "physician.updated", event);
    }

    private void updatePhysicianFromRequest(Physician physician, UpdatePhysicianCommand command) {
        if (command.getRequest().getName() != null) {
            physician.setName(command.getRequest().getName());
        }
        if (command.getRequest().getSpecialty() != null) {
            physician.setSpecialty(command.getRequest().getSpecialty());
        }
        // ... Lógica de atualização completa
    }
}
