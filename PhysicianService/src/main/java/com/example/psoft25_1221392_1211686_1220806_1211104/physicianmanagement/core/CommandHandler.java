package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core;

// T é o tipo específico de Command que este Handler manipula.
public interface CommandHandler<T extends Command> {
    void handle(T command);
}