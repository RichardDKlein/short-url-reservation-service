/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dto;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

@SuppressWarnings("unused")
public class StatusAndShortUrlReservationArray {
    private Status status;
    private List<ShortUrlReservation> shortUrlReservations;

    public StatusAndShortUrlReservationArray() {
    }

    public StatusAndShortUrlReservationArray(
            Status status,
            List<ShortUrlReservation> shortUrlReservations) {

        this.status = status;
        this.shortUrlReservations = shortUrlReservations;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<ShortUrlReservation> getShortUrlReservations() {
        return shortUrlReservations;
    }

    public void setShortUrlReservations(List<ShortUrlReservation> shortUrlReservations) {
        this.shortUrlReservations = shortUrlReservations;
    }
}
