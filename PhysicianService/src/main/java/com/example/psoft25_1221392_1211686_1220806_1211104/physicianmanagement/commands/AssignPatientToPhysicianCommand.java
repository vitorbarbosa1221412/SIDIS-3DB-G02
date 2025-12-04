package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.commands;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Command;

public class AssignPatientToPhysicianCommand implements Command {

    private final Long physicianId;
    private final Long patientId;

    public AssignPatientToPhysicianCommand(Long physicianId, Long patientId) {
        this.physicianId = physicianId;
        this.patientId = patientId;
    }

    // Getters para que o CommandHandler possa aceder aos dados
    public Long getPhysicianId() {
        return physicianId;
    }

    public Long getPatientId() {
        return patientId;
    }
}