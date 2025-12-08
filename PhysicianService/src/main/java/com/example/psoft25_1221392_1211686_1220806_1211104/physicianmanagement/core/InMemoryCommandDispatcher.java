package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Command;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.CommandHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;


@Component
public class InMemoryCommandDispatcher implements CommandDispatcher {


    private final Map<Class<? extends Command>, CommandHandler> handlers = new HashMap<>();


    public InMemoryCommandDispatcher() {

    }


    public void setHandlers(Map<Class<? extends Command>, CommandHandler> handlers) {
        this.handlers.clear();
        this.handlers.putAll(handlers);
        System.out.println("CQRS Dispatcher Mapped: " + handlers.size() + " Command Handlers.");
    }

    @Override
    public void dispatch(Command command) {
        CommandHandler handler = handlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No handler found for command: " + command.getClass().getName());
        }

        handler.handle(command);
    }
}