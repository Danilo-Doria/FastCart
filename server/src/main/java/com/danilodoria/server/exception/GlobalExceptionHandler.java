package com.danilodoria.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @RestControllerAdvice Indica a Spring que esta clase escuchará las excepciones
 * que ocurran en CUALQUIER controlador (@RestController) de la aplicación.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler Especifica qué tipo de excepción va a capturar este metodo.
    // En este caso, atrapará cualquier RuntimeException (o subclases de esta) lanzada en los controladores.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {

        // Se crea un mapa clave-valor para armar la estructura personalizada del JSON de respuesta.
        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());

        // Agrega el código de estado numérico (404)
        body.put("status", HttpStatus.NOT_FOUND.value());

        // Agrega la descripción corta del código HTTP
        body.put("error", "Not Found");

        // Agrega el mensaje de error personalizado que venía dentro de la excepción (ex.getMessage())
        body.put("message", ex.getMessage());

        // Devuelve el ResponseEntity especificando el estado 404 NOT FOUND
        // y adjunta el mapa 'body' para que Spring lo convierta en JSON.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // Maneja los errores de validación de los DTOs disparados por @Valid (400 BAD REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, Object> body = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        // Recorre todos los errores de cada campo que falló en el DTO
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Error de validación en los datos enviados");
        body.put("errors", errors); // Devuelve un mapa con el campo y el mensaje de error correspondiente

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", "Usuario o contraseña incorrectos");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}