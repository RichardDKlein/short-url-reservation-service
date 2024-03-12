/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.response;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

/**
 *
 */
public class StatusAndShortUrlReservationResponse {
    private StatusResponse status;
    private ShortUrlReservation shortUrlReservation;

    /**
     *
     * @param status
     * @param shortUrlReservation
     */
    public StatusAndShortUrlReservationResponse(
            StatusResponse status,
            ShortUrlReservation shortUrlReservation) {

        this.status = status;
        this.shortUrlReservation = shortUrlReservation;
    }
}
