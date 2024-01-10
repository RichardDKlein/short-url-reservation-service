package com.richarddklein.shorturlreservationservice.service;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

public interface ShortUrlReservationService {
    void initializeShortUrlReservationTable();
    List<ShortUrlReservation> getShortUrlReservationTable();
    String reserveAnyShortUrl();
    void reserveSpecificShortUrl(String shortUrl);
    void cancelShortUrlReservation(String shortUrl);
}
