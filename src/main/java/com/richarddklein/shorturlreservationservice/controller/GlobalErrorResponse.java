package com.richarddklein.shorturlreservationservice.controller;

import org.springframework.http.HttpStatus;

public class GlobalErrorResponse {

    private HttpStatus httpStatus;
    private String message;
    private String details;

    public GlobalErrorResponse(
            HttpStatus httpStatus, String message, String details) {

        this.httpStatus = httpStatus;
        this.message = message;
        this.details = details;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "GlobalErrorResponse{" +
                "httpStatusCode=" + httpStatus +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
