package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Command;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.services.UpdatePhysicianRequest;

/**
 * Command para atualizar um Médico existente.
 */
public class UpdatePhysicianCommand implements Command {

    private final String physicianNumber;
    private final UpdatePhysicianRequest request;

    public UpdatePhysicianCommand(String physicianNumber, UpdatePhysicianRequest request) {
        this.physicianNumber = physicianNumber;
        this.request = request;
    }

    // Getters para que o CommandHandler possa aceder aos dados
    public String getPhysicianNumber() {
        return physicianNumber;
    }

    public UpdatePhysicianRequest getRequest() {
        return request;
    }
}
