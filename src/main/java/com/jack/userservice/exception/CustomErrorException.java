package com.jack.userservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomErrorException extends RuntimeException {
    private final int statusCode;
    private final String status;
    private final String message;
    private final String path;

    public CustomErrorException(int statusCode, String status, String message, String path) {
        super(message);
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        this.path = path;
    }
}
