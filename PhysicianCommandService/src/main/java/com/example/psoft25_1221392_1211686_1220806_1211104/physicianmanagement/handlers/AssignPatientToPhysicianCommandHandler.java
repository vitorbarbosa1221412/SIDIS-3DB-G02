package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.handlers;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.AssignPatientToPhysicianCommand;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.events.PatientAssignedToPhysicianEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianWriteRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.infrastructure.EventPublisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Component
public class AssignPatientToPhysicianCommandHandler extends BaseCommandHandler<AssignPatientToPhysicianCommand> {

    private final PhysicianWriteRepository physicianWriteRepository;
    private final EventPublisher eventPublisher;

    @Autowired
    public AssignPatientToPhysicianCommandHandler(
            PhysicianWriteRepository physicianWriteRepository,
            EventPublisher eventPublisher)
    {
        this.physicianWriteRepository = physicianWriteRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void handle(AssignPatientToPhysicianCommand command) {

        // 1. EXECUÇÃO DA LÓGICA (WRITE DB - POSTGRESQL)
        Physician physician = physicianWriteRepository.findById(command.getPhysicianId())
                .orElseThrow(() -> new NoSuchElementException("Physician not found with ID: " + command.getPhysicianId()));

        // **Lógica de associação aqui**
        // physician.assignPatient(command.getPatientId());

        physicianWriteRepository.save(physician);

        // 2. PUBLICAR EVENTO PARA COMUNICAÇÃO INTER-SERVIÇOS (RABBITMQ)
        PatientAssignedToPhysicianEvent event = new PatientAssignedToPhysicianEvent(
                command.getPhysicianId(),
                command.getPatientId()
        );
        eventPublisher.publish("patient.events", "patient.assigned", event);
    }
}
