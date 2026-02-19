package com.baratieri.automasterbaratieri.controller.exceptions;

import com.baratieri.automasterbaratieri.services.exceptions.RegraNegocioException;
import com.baratieri.automasterbaratieri.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> dataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        StandardError err = StandardError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error("Violação de integridade de dados")
                .message("Dados duplicados ou relação inválida no banco de dados. Verifique se o registro já existe.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(
            ResourceNotFoundException e,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = StandardError.builder()

                .timestamp(Instant.now())
                .status(status.value())
                .error("Recurso não encontrado")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(err);

    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<StandardError> regraNegocio(RegraNegocioException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError error = StandardError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error("Regra de negócio")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String nomeParametro = e.getName();
        Object valorDigitado = e.getValue();

        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError error = StandardError.builder()
        .timestamp(Instant.now())
        .status(status.value())
        .error("Parâmetro de URL inválido")
        .message("O valor '" + valorDigitado + "' não é válido para o parâmetro '" + nomeParametro + "'.")

           .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        ValidationError err = new ValidationError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("Erro de Validação");
        err.setMessage("Campos inválidos no formulário");
        err.setPath(request.getRequestURI());

        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(err);
    }

}
