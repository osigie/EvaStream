package com.osigie.tracker_service.exceptions;

import com.osigie.tracker_service.dto.ErrorResponseDto;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {PeerNotRegisteredException.class})
    public ResponseEntity<ErrorResponseDto> peerNotRegisteredException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponseDto(
                request.getDescription(false),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponseDto> globalException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponseDto(
                request.getDescription(false),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now()
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
