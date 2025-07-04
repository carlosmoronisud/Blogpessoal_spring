// src/main/java/com/generation/blogpessoal/exception/GlobalExceptionHandler.java
package com.generation.blogpessoal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletRequest; // Importe esta classe se decidir usar

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) { // Adicione HttpServletRequest
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        problemDetail.setTitle("Erro na Requisição");
        problemDetail.setType(URI.create("https://spring.io/problems/request-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getRequestURI()); // Use request.getRequestURI()

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) { // Adicione HttpServletRequest
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Campos Inválidos");
        problemDetail.setType(URI.create("https://spring.io/problems/invalid-fields"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getRequestURI()); // Use request.getRequestURI()

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Erro Interno do Servidor");
        problemDetail.setDetail("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.");
        problemDetail.setType(URI.create("https://spring.io/problems/internal-server-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        // Em um ambiente de produção, você não deve expor a mensagem da exceção genérica diretamente.
        // Para debug em ambiente de desenvolvimento, você pode adicionar:
        // problemDetail.setProperty("debugMessage", ex.getMessage()); 
        return problemDetail;
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Parâmetro inválido");
        problemDetail.setDetail("O parâmetro '" + ex.getName() + "' deve ser do tipo " + ex.getRequiredType().getSimpleName());
        problemDetail.setType(URI.create("https://spring.io/problems/invalid-parameter"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());

        return problemDetail;
    }
}