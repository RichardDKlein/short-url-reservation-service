/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.response;

/**
 *
 */
public class StatusResponse {
    private ShortUrlReservationStatus status;
    private String message;

    public StatusResponse(ShortUrlReservationStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
