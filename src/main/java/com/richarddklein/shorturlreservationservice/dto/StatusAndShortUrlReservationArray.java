/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dto;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.response.ShortUrlReservationStatus;

@SuppressWarnings("unused")
public class StatusAndShortUrlReservationArray {
    private ShortUrlReservationStatus status;
    private List<ShortUrlReservation> shortUrlReservations;

    public StatusAndShortUrlReservationArray() {
    }

    public StatusAndShortUrlReservationArray(
            ShortUrlReservationStatus status,
            List<ShortUrlReservation> shortUrlReservations) {

        this.status = status;
        this.shortUrlReservations = shortUrlReservations;
    }

    public ShortUrlReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ShortUrlReservationStatus status) {
        this.status = status;
    }

    public List<ShortUrlReservation> getShortUrlReservations() {
        return shortUrlReservations;
    }

    public void setShortUrlReservations(List<ShortUrlReservation> shortUrlReservations) {
        this.shortUrlReservations = shortUrlReservations;
    }
}
