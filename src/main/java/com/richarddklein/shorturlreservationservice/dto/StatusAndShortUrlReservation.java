/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dto;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

@SuppressWarnings("unused")
public class StatusAndShortUrlReservation {
    private Status status;
    private ShortUrlReservation shortUrlReservation;

    public StatusAndShortUrlReservation() {
    }

    public StatusAndShortUrlReservation(
            Status status,
            ShortUrlReservation shortUrlReservation) {

        this.status = status;
        this.shortUrlReservation = shortUrlReservation;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ShortUrlReservation getShortUrlReservation() {
        return shortUrlReservation;
    }

    public void setShortUrlReservation(ShortUrlReservation shortUrlReservation) {
        this.shortUrlReservation = shortUrlReservation;
    }
}
