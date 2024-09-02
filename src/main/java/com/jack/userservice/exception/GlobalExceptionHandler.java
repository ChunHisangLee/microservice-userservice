package com.jack.userservice.exception;

import com.jack.userservice.response.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomErrorException.class)
    public ResponseEntity<CustomErrorResponse> handleCustomError(CustomErrorException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                ex.getStatusCode(),
                ex.getStatus(),
                ex.getMessage(),
                ex.getPath()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getStatus()));
    }
}
