package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Query;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.QueryHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryQueryDispatcher implements QueryDispatcher {

    private final Map<Class<? extends Query>, QueryHandler> handlers = new HashMap<>();

    @Autowired
    public InMemoryQueryDispatcher(ApplicationContext applicationContext) {
        // Encontra todos os beans que implementam QueryHandler
        Map<String, QueryHandler> handlersMap = applicationContext.getBeansOfType(QueryHandler.class);

        handlersMap.values().forEach(handler -> {
            // Lógica para extrair o tipo da Query que o Handler manipula
            Class<?> queryType = (Class<?>) ((ParameterizedType) handler.getClass().getGenericInterfaces()[0])
                    .getActualTypeArguments()[0];
            handlers.put((Class<? extends Query>) queryType, handler);
        });
    }

    public void setHandlers(Map<Class<? extends Query>, QueryHandler> handlers) {
        this.handlers.clear();
        this.handlers.putAll(handlers);
        System.out.println("CQRS Dispatcher Mapped: " + handlers.size() + " Query Handlers.");
    }
    @Override
    public <R> R dispatch(Query<R> query) {
        QueryHandler handler = handlers.get(query.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No handler found for query: " + query.getClass().getName());
        }
        // Chama o método handle e retorna o resultado
        return (R) handler.handle(query);
    }
}