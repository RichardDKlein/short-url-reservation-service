package com.richarddklein.shorturlreservationservice.service;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;

public interface ShortUrlReservationService {
    void initializeShortUrlReservationRepository();
    List<ShortUrlReservation> getAllShortUrlReservations();
    ShortUrlReservation getSpecificShortUrlReservation(String shortUrl);
    ShortUrlReservation reserveAnyShortUrl() throws NoShortUrlsAvailableException;
    void reserveSpecificShortUrl(String shortUrl);
    void cancelShortUrlReservation(String shortUrl);
}
