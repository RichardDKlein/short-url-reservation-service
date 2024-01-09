package com.richarddklein.shorturlreservationservice.response;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

public class StatusAndShortUrlReservationArrayResponse {

    private StatusResponse status;
    private List<ShortUrlReservation> shortUrlReservations;

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
