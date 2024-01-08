package com.richarddklein.shorturlreservationservice.service;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

public interface ShortUrlReservationService {
    void initializeShortUrlReservationsTable();
    List<ShortUrlReservation> getShortUrlReservationsTable();
    String reserveAnyShortUrl();
    void reserveSpecifiedShortUrl(String shortUrl);
    void cancelShortUrlReservation(String shortUrl);
}
