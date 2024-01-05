package com.richarddklein.shorturlreservationservice.service;

public interface ShortUrlReservationService {
    void initializeShortUrlReservationsTable();
    String reserveAnyShortUrl();
    void reserveSpecifiedShortUrl(String shortUrl);
    void cancelShortUrlReservation(String shortUrl);
}
