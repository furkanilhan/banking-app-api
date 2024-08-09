package com.furkan.banking.exception;

import com.furkan.banking.payload.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGlobalException(Exception ex) {
        logger.error("Internal Server Error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Internal Server Error"));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<MessageResponse> handleCustomException(CustomException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(new MessageResponse(ex.getMessage()));
    }
}
