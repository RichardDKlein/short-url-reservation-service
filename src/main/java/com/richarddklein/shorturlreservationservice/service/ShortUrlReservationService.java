package com.richarddklein.shorturlreservationservice.service;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

public interface ShortUrlReservationService {
    void initializeShortUrlReservationRepository();
    List<ShortUrlReservation> getAllShortUrlReservations();
    ShortUrlReservation getSpecificShortUrlReservation(String shortUrl);
    String reserveAnyShortUrl();
    void reserveSpecificShortUrl(String shortUrl);
    void cancelShortUrlReservation(String shortUrl);
}
