package com.example.rqchallenge.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    //TODO: Handle custom business logic errors

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ResponseBody<?>> handleRestClientResponseException(RestClientResponseException ex) {
        log.error("Error from external API", ex);
        ResponseBody<?> responseBody = new ResponseBody<>();
        responseBody.setStatus(HttpStatus.valueOf(ex.getRawStatusCode()).name());
        responseBody.setMessage(ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.valueOf(ex.getRawStatusCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Invalid client input", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }


}