package com.parde4.climaapi.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarRuntimeException(RuntimeException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", 400);
        error.put("error", "Error de negocio");
        error.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {

    Map<String, Object> response = new HashMap<>();
    Map<String, String> errores = new HashMap<>();
    Map<String, Integer> prioridades = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error -> {
        String campo = error.getField();
        String codigo = error.getCode();

        int prioridad = switch (codigo) {
            case "NotBlank" -> 3;
            case "Size" -> 2;
            default -> 1;
        };

        if (!prioridades.containsKey(campo) || prioridad > prioridades.get(campo)) {
            prioridades.put(campo, prioridad);
            errores.put(campo, error.getDefaultMessage());
        }
    });

    response.put("timestamp", LocalDateTime.now());
    response.put("status", 400);
    response.put("error", "Error de validación");
    response.put("mensajes", errores);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<Map<String, Object>> manejarCiudadNoEncontrada(
            HttpClientErrorException.NotFound ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", 404);
        error.put("error", "Ciudad no encontrada");
        error.put("mensaje",
                "No se encontro informacion climatica para la ciudad ingresada , cheque bien el nombre e intente nuevamente.");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<Map<String, Object>> manejarApiKeyInvalida(
            HttpClientErrorException.Unauthorized ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", 401);
        error.put("error", "Error de autenticación");
        error.put("mensaje",
                "La API Key de OpenWeatherMap es inválida o no está configurada correctamente.");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExceptionGeneral(Exception ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", 500);
        error.put("error", "Error interno del servidor");
        error.put("mensaje",
                "Ocurrió un problema en el sistema , intentelo más tarde.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}