package com.example.psoft25_1221392_1211686_1220806_1211104.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.MalformedURLException;


@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Object Not Found")
public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(final String string) {
        super(string);
    }

    public NotFoundException(final String string, final MalformedURLException ex) {
        super(string, ex);
    }

    public NotFoundException(final Class<?> clazz, final long id) {
        super(String.format("Entity %s with id %d not found", clazz.getSimpleName(), id));
    }

    public NotFoundException(final Class<?> clazz, final String id) {
        super(formatMessage(clazz, id));
    }

    private static String formatMessage(final Class<?> clazz, final String id) {
        String idType = "id";
        if (String.class.equals(clazz)) {
            idType = "patientId";
        }
        return String.format("Entity %s with %s %s not found", clazz.getSimpleName(), idType, id);
    }
}
