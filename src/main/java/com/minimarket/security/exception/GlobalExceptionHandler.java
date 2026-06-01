package com.minimarket.security.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(403).body(Map.of(
            "error", "Acceso denegado",
            "mensaje", "No tienes permisos para acceder a este recurso"
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleUnauthorized(AuthenticationException ex) {
        return ResponseEntity.status(401).body(Map.of(
            "error", "No autenticado",
            "mensaje", "Debes iniciar sesión para acceder a este recurso"
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        return ResponseEntity.status(500).body(Map.of(
            "error", "Error interno",
            "mensaje", "Ocurrió un error inesperado"
        ));
    }

    

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(java.util.stream.Collectors.joining(", "));

        return ResponseEntity.status(400).body(Map.of(
            "error", "Datos inválidos",
            "mensaje", mensaje
        ));
    }
}