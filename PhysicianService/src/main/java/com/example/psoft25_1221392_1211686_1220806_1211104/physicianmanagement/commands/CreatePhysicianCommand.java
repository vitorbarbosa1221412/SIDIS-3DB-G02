package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Command;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services.CreatePhysicianRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * Command para criar um novo médico.
 * Implementa a interface Command do CQRS Core.
 */
public class CreatePhysicianCommand implements Command {

    private final CreatePhysicianRequest request;
    private final MultipartFile imageFile;

    public CreatePhysicianCommand(CreatePhysicianRequest request, MultipartFile imageFile) {
        this.request = request;
        this.imageFile = imageFile;
    }

    // Getters para que o CommandHandler possa aceder aos dados
    public CreatePhysicianRequest getRequest() {
        return request;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }
}