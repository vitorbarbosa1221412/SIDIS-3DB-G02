package com.example.psoft25_1221392_1211686_1220806_1211104.Config;

// APENAS IMPORTS DE QUERY (Nada de Commands)
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.QueryHandler;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Query;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.InMemoryQueryDispatcher;

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

    // REMOVIDO: InMemoryCommandDispatcher (Este serviço não processa comandos)
    private final InMemoryQueryDispatcher queryDispatcher;

    @Autowired
    public CqrsConfig(ApplicationContext applicationContext,
                      // REMOVIDO: InMemoryCommandDispatcher do construtor
                      InMemoryQueryDispatcher queryDispatcher) {
        this.applicationContext = applicationContext;
        this.queryDispatcher = queryDispatcher;
    }

    @PostConstruct
    public void registerHandlers() {
        // REMOVIDO: mapAndSetCommandHandlers();
        mapAndSetQueryHandlers();
    }

    // MÉTODO DE COMANDOS APAGADO POR COMPLETO

    // LÓGICA PARA QUERY HANDLERS (MANTIDA)
    @SuppressWarnings("unchecked")
    private void mapAndSetQueryHandlers() {
        Map<Class<? extends Query>, QueryHandler> queryMap = new HashMap<>();

        Map<String, QueryHandler> handlersMap = applicationContext.getBeansOfType(QueryHandler.class);
        Class<QueryHandler> targetInterface = QueryHandler.class;

        handlersMap.values().forEach(handler -> {
            // Varre as interfaces para descobrir qual Query este Handler trata
            for (java.lang.reflect.Type type : handler.getClass().getGenericInterfaces()) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    if (parameterizedType.getRawType().equals(targetInterface)) {
                        try {
                            Class<?> queryType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                            queryMap.put((Class<? extends Query>) queryType, handler);
                        } catch (Exception e) {
                            System.err.println("Erro ao mapear QueryHandler: " + e.getMessage());
                        }
                    }
                }
            }
        });


         queryDispatcher.setHandlers(queryMap);

        // Alternativa se o dispatcher não tiver setter:
        // O ideal seria passar o map no construtor do Bean, mas como estamos a usar PostConstruct:
        // Certifica-te que InMemoryQueryDispatcher tem: public void setHandlers(Map<Class<? extends Query>, QueryHandler> handlers)

        System.out.println("CQRS Dispatcher Mapped: " + queryMap.size() + " Query Handlers.");
    }
}
