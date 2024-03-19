/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.response;

/**
 * The Global Error Response class.
 */
public class GlobalErrorResponse {
    private int httpStatusCode;
    private String message;
    private String details;

    /**
     * General constructor.
     *
     * @param httpStatusCode The HTTP Status code for the error.
     * @param message A message briefly describing the error.
     * @param details A string giving more details about the error.
     */
    public GlobalErrorResponse(int httpStatusCode, String message, String details) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
        this.details = details;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
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
                "httpStatusCode=" + httpStatusCode +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
