/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.response;

/**
 * Class defining an HTTP Response containing a status
 * code/message only.
 */
public class StatusResponse {
    private ShortUrlReservationStatus status;
    private String message;

    /**
     * General constructor.
     *
     * @param status The status code to be embedded in the HTTP Response.
     * @param message The status message to be embedded in the HTTP Response.
     */
    public StatusResponse(ShortUrlReservationStatus status, String message) {
        this.status = status;
        this.message = message;
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
        return "StatusResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
