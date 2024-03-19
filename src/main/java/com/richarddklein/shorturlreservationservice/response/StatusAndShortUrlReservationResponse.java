/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.response;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

/**
 * Class defining an HTTP Response containing a status
 * code/message as well as a Short URL Reservation entity.
 */
public class StatusAndShortUrlReservationResponse {
    private StatusResponse status;
    private ShortUrlReservation shortUrlReservation;

    /**
     * General constructor.
     *
     * @param status The status code/message to be embedded
     *               in the HTTP Response.
     * @param shortUrlReservation The Short URL Reservation
     *                            entity to be embedded in
     *                            the HTTP Response.
     */
    public StatusAndShortUrlReservationResponse(
            StatusResponse status,
            ShortUrlReservation shortUrlReservation) {

        this.status = status;
        this.shortUrlReservation = shortUrlReservation;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }

    public ShortUrlReservation getShortUrlReservation() {
        return shortUrlReservation;
    }

    public void setShortUrlReservation(ShortUrlReservation shortUrlReservation) {
        this.shortUrlReservation = shortUrlReservation;
    }

    @Override
    public String toString() {
        return "StatusAndShortUrlReservationResponse{" +
                "status=" + status +
                ", shortUrlReservation=" + shortUrlReservation +
                '}';
    }
}
