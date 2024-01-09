package com.richarddklein.shorturlreservationservice.response;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

public class StatusAndShortUrlReservationResponse {

    private StatusResponse status;
    private ShortUrlReservation shortUrlReservation;

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
