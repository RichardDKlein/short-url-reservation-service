package com.richarddklein.shorturlreservationservice.exception;

import com.richarddklein.shorturlreservationservice.response.GlobalErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException e) {
        logger.warn("====> ", e);
        GlobalErrorResponse globalErrorResponse = new GlobalErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                e.getMessage());

        return new ResponseEntity<>(globalErrorResponse,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResponse> handleAllOtherExceptions(Exception e) {
        logger.warn("====> ", e);
        GlobalErrorResponse globalErrorResponse = new GlobalErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                e.getMessage());

        return new ResponseEntity<>(globalErrorResponse,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
