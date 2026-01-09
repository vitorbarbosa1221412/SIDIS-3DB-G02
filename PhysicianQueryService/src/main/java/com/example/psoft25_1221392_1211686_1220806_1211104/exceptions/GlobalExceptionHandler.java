package com.example.psoft25_1221392_1211686_1220806_1211104.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LogManager.getLogger();

    @ExceptionHandler(value = { OptimisticLockingFailureException.class, ConflictException.class })
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<Object> handleConflict(final HttpServletRequest request, final Exception ex) {
        logger.error("ConflictException {}\n", request.getRequestURI(), ex);

        final Map<String, String> details = new HashMap<>();
        details.put("message", "Object was updated by another user");
        details.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({ DuplicateKeyException.class })
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<Object> handleDuplicateKeyException(final HttpServletRequest request,
                                                                 final DuplicateKeyException ex) {
        logger.error("DuplicateKeyException {}\n", request.getRequestURI(), ex);

        final Map<String, String> details = new HashMap<>();
        details.put("message", "The identity of the object you tried to create is already in use");
        details.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<Object> handleDataIntegrityViolation(final HttpServletRequest request,
                                                                  final DataIntegrityViolationException ex) {
        logger.error("DataIntegrityViolationException {}\n", request.getRequestURI(), ex);

        final Map<String, String> details = new HashMap<>();
        details.put("message", "Data integrity violation");
        details.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({ IllegalArgumentException.class, NumberFormatException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleIllegalArgument(final HttpServletRequest request,
                                                           final IllegalArgumentException ex) {
        logger.error("BadRequestException {}\n", request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiCallError<String>> handleNotFoundException(final HttpServletRequest request,
                                                                        final NotFoundException ex) {
        logger.error("NotFoundException {}\n", request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiCallError<String>> handleValidationException(final HttpServletRequest request,
                                                                          final ValidationException ex) {
        logger.error("ValidationException {}\n", request.getRequestURI(), ex);

        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiCallError<Map.Entry<String, String>>> handleMethodArgumentTypeMismatchException(
            final HttpServletRequest request, final MethodArgumentTypeMismatchException ex) {
        logger.error("handleMethodArgumentTypeMismatchException {}\n", request.getRequestURI(), ex);

        final Map<String, String> details = new HashMap<>();
        details.put("paramName", ex.getName());
        details.put("paramValue", Optional.ofNullable(ex.getValue()).map(Object::toString).orElse(""));
        details.put("errorMessage", ex.getMessage());

        return ResponseEntity.badRequest().build();
    }

    /**
     * handle validation errors generated by @Valid
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.error("handleMethodArgumentNotValidException\n", ex);

        final List<Map<String, String>> details = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            final Map<String, String> detail = new HashMap<>();
            detail.put("objectName", fieldError.getObjectName());
            detail.put("field", fieldError.getField());
            detail.put("rejectedValue", "" + fieldError.getRejectedValue());
            detail.put("errorMessage", fieldError.getDefaultMessage());
            details.add(detail);
        });

        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiCallError<String>> handleAccessDeniedException(final HttpServletRequest request,
                                                                            final AccessDeniedException ex) {
        logger.error("handleAccessDeniedException {}\n", request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiCallError<T> {
        private String message;
        private Collection<T> details;
    }
}