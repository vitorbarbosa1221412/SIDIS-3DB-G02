package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.handlers;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands.CreatePhysicianCommand;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.events.PhysicianCreatedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model.Physician;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianWriteRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.infrastructure.EventPublisher;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services.CreatePhysicianRequest;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CreatePhysicianCommandHandler extends BaseCommandHandler<CreatePhysicianCommand> {

    private final PhysicianWriteRepository physicianWriteRepository;
    private final UserService userService;
    private final EventPublisher eventPublisher;

    @Autowired
    public CreatePhysicianCommandHandler(
            PhysicianWriteRepository physicianWriteRepository,
            UserService userService,
            EventPublisher eventPublisher)
    {
        this.physicianWriteRepository = physicianWriteRepository;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void handle(CreatePhysicianCommand command) {
        // 1. EXECUÇÃO DA LÓGICA (WRITE DB - POSTGRESQL)
        Physician physician = mapToPhysicianEntity(command.getRequest(), command.getImageFile());
        Physician savedPhysician = physicianWriteRepository.save(physician);

        // 2. PUBLICAR EVENTO PARA PROJEÇÃO (RABBITMQ)
        PhysicianCreatedEvent event = new PhysicianCreatedEvent(
                savedPhysician.getId(),
                savedPhysician.getPhysicianNumber(),
                savedPhysician.getName(),
                savedPhysician.getSpecialty()
        );
        eventPublisher.publish("physician_events", "physician.created", event);
    }

    private Physician mapToPhysicianEntity(CreatePhysicianRequest request, MultipartFile imageFile) {
        // Lógica de mapeamento real, assumindo a existência do construtor/setters
        return new Physician();
    }
}
