/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dto;

/**
 * Class defining an HTTP Response containing a status
 * code/message only.
 */
public class Status {
    private ShortUrlReservationStatus status;
    private String message;

    public Status(ShortUrlReservationStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status(ShortUrlReservationStatus status) {
        this.status = status;
    }

    public ShortUrlReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ShortUrlReservationStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Status{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
