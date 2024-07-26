/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dto;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.response.ShortUrlReservationStatus;

@SuppressWarnings("unused")
public class StatusAndShortUrlReservation {
    private ShortUrlReservationStatus status;
    private ShortUrlReservation shortUrlReservation;

    public StatusAndShortUrlReservation() {
    }

    public StatusAndShortUrlReservation(
            ShortUrlReservationStatus status,
            ShortUrlReservation shortUrlReservation) {

        this.status = status;
        this.shortUrlReservation = shortUrlReservation;
    }

    public ShortUrlReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ShortUrlReservationStatus status) {
        this.status = status;
    }

    public ShortUrlReservation getShortUrlReservation() {
        return shortUrlReservation;
    }

    public void setShortUrlReservation(ShortUrlReservation shortUrlReservation) {
        this.shortUrlReservation = shortUrlReservation;
    }
}
