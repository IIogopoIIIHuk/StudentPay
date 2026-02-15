package com.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<AppError> handleServiceException(ServiceException e) {
        return new ResponseEntity<>(new AppError(e.getStatus().value(), e.getMessage()), e.getStatus());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<AppError> handleNotFound(NoSuchElementException e) {
        return new ResponseEntity<>(new AppError(404, e.getMessage()), org.springframework.http.HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<AppError> handleIllegalState(IllegalStateException e) {
        return new ResponseEntity<>(new AppError(400, e.getMessage()), org.springframework.http.HttpStatus.BAD_REQUEST);
    }
}