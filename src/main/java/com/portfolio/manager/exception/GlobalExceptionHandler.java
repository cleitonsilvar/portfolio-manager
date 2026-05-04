package com.portfolio.manager.exception;

import com.portfolio.manager.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getStatusCode()).body(
            ErrorResponse.builder()
                .error(ex.getClass().getSimpleName())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Inválido",
                (first, second) -> first
            ));
        return ResponseEntity.badRequest().body(
            ErrorResponse.builder()
                .error("ValidationError")
                .message("Erro de validação nos campos da requisição")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .errors(errors)
                .build()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
            ErrorResponse.builder()
                .error("ConstraintViolation")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
            ErrorResponse.builder()
                .error("BadRequest")
                .message("Corpo da requisição inválido ou mal formatado")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return ResponseEntity.status(405).body(
            ErrorResponse.builder()
                .error("MethodNotAllowed")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Erro interno não tratado: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(
            ErrorResponse.builder()
                .error("InternalServerError")
                .message("Erro interno do servidor")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build()
        );
    }
}
