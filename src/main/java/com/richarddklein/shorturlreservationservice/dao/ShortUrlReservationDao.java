package com.richarddklein.shorturlreservationservice.dao;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

public interface ShortUrlReservationDao {
    void createAllShortUrlReservations();
    List<ShortUrlReservation> readAllShortUrlReservations();
    ShortUrlReservation readShortUrlReservation(String shortUrl);
    ShortUrlReservation updateShortUrlReservation(ShortUrlReservation shortUrlReservation);
}
