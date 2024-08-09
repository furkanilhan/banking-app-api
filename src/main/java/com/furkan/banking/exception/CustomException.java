package com.furkan.banking.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomException extends RuntimeException {
    private final HttpStatus status;
    private Throwable ex;

    public CustomException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public CustomException(HttpStatus status, String message, Throwable ex) {
        super(message);
        this.status = status;
        this.ex = ex;
    }
}
