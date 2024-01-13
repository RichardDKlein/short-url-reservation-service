package com.richarddklein.shorturlreservationservice.response;

import com.richarddklein.shorturlreservationservice.util.ShortUrlReservationStatus;

public class StatusResponse {

    private ShortUrlReservationStatus status;
    private String message;

    public StatusResponse(ShortUrlReservationStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ShortUrlReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ShortUrlReservationStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
