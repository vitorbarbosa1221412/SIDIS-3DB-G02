package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryQueryDispatcher implements QueryDispatcher {

    private final Map<Class<? extends Query>, QueryHandler> handlers = new HashMap<>();


    public InMemoryQueryDispatcher() {
    }

    // Este método é chamado pelo CqrsConfig para preencher o mapa
    public void setHandlers(Map<Class<? extends Query>, QueryHandler> handlers) {
        this.handlers.clear();
        this.handlers.putAll(handlers);
        System.out.println("CQRS Dispatcher Mapped: " + handlers.size() + " Query Handlers.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R dispatch(Query<R> query) {
        QueryHandler handler = handlers.get(query.getClass());

        if (handler == null) {
            throw new IllegalArgumentException("No handler found for query: " + query.getClass().getName());
        }

        // Chama o método handle e retorna o resultado
        return (R) handler.handle(query);
    }
}