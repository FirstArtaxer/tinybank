package com.artaxer.tinybank.exception;

import com.artaxer.tinybank.dto.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(new ExceptionResponseDto(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ExceptionResponseDto> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(new ExceptionResponseDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ExceptionResponseDto> handleAuthorizationException(AuthorizationDeniedException ex) {
        return new ResponseEntity<>(new ExceptionResponseDto(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleGlobalException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(new ExceptionResponseDto(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
