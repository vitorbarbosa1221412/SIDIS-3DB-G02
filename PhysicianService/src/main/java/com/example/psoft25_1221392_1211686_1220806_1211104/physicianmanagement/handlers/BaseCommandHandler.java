package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.handlers;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Command;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.CommandHandler;

import java.lang.reflect.ParameterizedType;

// Classe base abstrata para todos os Command Handlers
// Implementa a lógica de descoberta de tipos de forma segura
public abstract class BaseCommandHandler<T extends Command> implements CommandHandler<T> {


    private final Class<T> commandType;

    protected BaseCommandHandler() {
        // Usa a reflexão para obter o tipo real de 'T' (o Command) no runtime
        // NOTE: Isto funciona porque a classe *extende* BaseCommandHandler<T>
        this.commandType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public Class<T> getCommandType() {
        return commandType;
    }
}
