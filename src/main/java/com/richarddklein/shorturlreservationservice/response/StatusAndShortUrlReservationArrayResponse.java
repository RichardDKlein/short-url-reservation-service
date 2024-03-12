/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.response;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

/**
 *
 */
public class StatusAndShortUrlReservationArrayResponse {
    private StatusResponse status;
    private List<ShortUrlReservation> shortUrlReservations;

    /**
     *
     * @param status
     * @param shortUrlReservations
     */
    public StatusAndShortUrlReservationArrayResponse(
            StatusResponse status,
            List<ShortUrlReservation> shortUrlReservations) {

        this.status = status;
        this.shortUrlReservations = shortUrlReservations;
    }
}
