/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.response;

/**
 *
 */
public class GlobalErrorResponse {
    private int httpStatusCode;
    private String message;
    private String details;

    /**
     *
     * @param httpStatusCode
     * @param message
     * @param details
     */
    public GlobalErrorResponse(int httpStatusCode, String message, String details) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
        this.details = details;
    }
}
