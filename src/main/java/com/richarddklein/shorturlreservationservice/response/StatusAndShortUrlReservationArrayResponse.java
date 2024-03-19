/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.response;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

/**
 * Class defining an HTTP Response containing a status code/message
 * as well as an array of Short URL Reservation entities.
 */
public class StatusAndShortUrlReservationArrayResponse {
    private StatusResponse status;
    private List<ShortUrlReservation> shortUrlReservations;

    /**
     * General constructor.
     *
     * @param status The status code/message to be embedded in the
     *               HTTP Response.
     * @param shortUrlReservations The array of Short URL Reservation
     *                             entities to be embedded in the HTTP
     *                             Response.
     */
    public StatusAndShortUrlReservationArrayResponse(
            StatusResponse status,
            List<ShortUrlReservation> shortUrlReservations) {

        this.status = status;
        this.shortUrlReservations = shortUrlReservations;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }

    public List<ShortUrlReservation> getShortUrlReservations() {
        return shortUrlReservations;
    }

    public void setShortUrlReservations(List<ShortUrlReservation> shortUrlReservations) {
        this.shortUrlReservations = shortUrlReservations;
    }

    @Override
    public String toString() {
        return "StatusAndShortUrlReservationArrayResponse{" +
                "status=" + status +
                ", shortUrlReservations=" + shortUrlReservations +
                '}';
    }
}
