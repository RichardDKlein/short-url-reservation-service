package com.richarddklein.shorturlreservationservice.dao;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;

public interface ShortUrlReservationDao {
    void createAllShortUrlReservations();
    List<ShortUrlReservation> readAllShortUrlReservations();
    ShortUrlReservation readShortUrlReservation(String shortUrl);
    ShortUrlReservation updateShortUrlReservation(ShortUrlReservation shortUrlReservation);
    ShortUrlReservation findAvailableShortUrlReservation() throws NoShortUrlsAvailableException;
}
