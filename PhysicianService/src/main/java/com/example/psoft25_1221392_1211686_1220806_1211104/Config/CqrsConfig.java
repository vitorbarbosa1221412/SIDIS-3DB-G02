package com.example.psoft25_1221392_1211686_1220806_1211104.Config;


import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.CommandHandler;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Command;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.InMemoryCommandDispatcher;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.QueryHandler;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Query;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.InMemoryQueryDispatcher;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.handlers.BaseCommandHandler; // NOVO IMPORT


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CqrsConfig {

    private final ApplicationContext applicationContext;
    private final InMemoryCommandDispatcher commandDispatcher;
    private final InMemoryQueryDispatcher queryDispatcher;

    @Autowired
    public CqrsConfig(ApplicationContext applicationContext,
                      InMemoryCommandDispatcher commandDispatcher,
                      InMemoryQueryDispatcher queryDispatcher) {
        this.applicationContext = applicationContext;
        this.commandDispatcher = commandDispatcher;
        this.queryDispatcher = queryDispatcher;
    }

    @PostConstruct
    public void registerHandlers() {
        mapAndSetCommandHandlers();
        mapAndSetQueryHandlers();
    }

    // LÓGICA CORRIGIDA PARA USAR BaseCommandHandler
    private void mapAndSetCommandHandlers() {
        Map<Class<? extends Command>, CommandHandler> commandMap = new HashMap<>();

        // Procuramos apenas por classes que estendem BaseCommandHandler (os nossos beans)
        Map<String, BaseCommandHandler> baseHandlers = applicationContext.getBeansOfType(BaseCommandHandler.class);

        baseHandlers.values().forEach(handler -> {
            try {
                // Usamos o método getCommandType() que é seguro e confiável
                Class<? extends Command> commandType = handler.getCommandType();
                commandMap.put(commandType, handler);
            } catch (Exception e) {
                // Log para debug, mas não deve ocorrer mais
                System.err.println("Erro Crítico ao mapear Command Handler " + handler.getClass().getName() + ": " + e.getMessage());
            }
        });
        commandDispatcher.setHandlers(commandMap);
        System.out.println("CQRS Dispatcher Mapped: " + commandMap.size() + " Command Handlers.");
    }

    // LÓGICA PARA QUERY HANDLERS (Ajustada para usar BaseQueryHandler, se existir)
    private void mapAndSetQueryHandlers() {
        Map<Class<? extends Query>, QueryHandler> queryMap = new HashMap<>();

        // Se você tiver uma classe BaseQueryHandler:
        // Map<String, BaseQueryHandler> baseHandlers = applicationContext.getBeansOfType(BaseQueryHandler.class);

        // Se você não tiver BaseQueryHandler, usamos a lógica original de reflexão (que já estava a funcionar)
        Map<String, QueryHandler> handlersMap = applicationContext.getBeansOfType(QueryHandler.class);
        Class<QueryHandler> targetInterface = QueryHandler.class;

        handlersMap.values().forEach(handler -> {
            for (java.lang.reflect.Type type : handler.getClass().getGenericInterfaces()) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    if (parameterizedType.getRawType().equals(targetInterface)) {
                        Class<?> queryType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        queryMap.put((Class<? extends Query>) queryType, handler);
                        return;
                    }
                }
            }
        });
        queryDispatcher.setHandlers(queryMap);
        System.out.println("CQRS Dispatcher Mapped: " + queryMap.size() + " Query Handlers.");
    }
}
