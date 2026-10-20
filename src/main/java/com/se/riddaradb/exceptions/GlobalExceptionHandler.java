package com.se.riddaradb.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex){

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        //The key of the map
                        FieldError::getField,
                        //The value of the map; if null, provide fallback
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation error",
                        //Merge rule to handle multiple values being placed in the map
                        (existing, replacement) -> existing
                ));

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Validation error encountered");
        problemDetail.setDetail("A field or fields in this request failed validation");
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleEntityNotFound(ResourceNotFoundException ex){

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Entity not found");
        problemDetail.setDetail("The requested entity could not be found in the database");
        problemDetail.setProperty("error", ex.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex){

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);

        problemDetail.setTitle("Unauthorised request");
        problemDetail.setDetail("The current user is unauthorised to make this request");
        problemDetail.setProperty("error", ex.getMessage());

        return problemDetail;
    }
}
