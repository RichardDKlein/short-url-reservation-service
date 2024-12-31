/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dao;

import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.ShortUrlReservationStatus;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservation;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservationArray;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.entity.ShortUrlReservation;
import reactor.core.publisher.Mono;

/**
 * The Short URL Reservation DAO (Data Access Object) interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * provides access to the data repository of the Short URL Reservation
 * Service.</p>
 */

public interface ShortUrlReservationDao {
    void initializeShortUrlReservationRepository();

    Mono<ShortUrlReservation>
    getSpecificShortUrlReservation(String shortUrl);

    Mono<StatusAndShortUrlReservationArray>
    getAllShortUrlReservations();

    Mono<StatusAndShortUrlReservation>
    reserveAnyShortUrl();

    Mono<ShortUrlReservationStatus>
    reserveSpecificShortUrl(String shortUrl);

    Mono<ShortUrlReservationStatus>
    reserveAllShortUrls();

    Mono<ShortUrlReservationStatus>
    cancelSpecificShortUrlReservation(String shortUrl);

    Mono<ShortUrlReservationStatus>
    cancelAllShortUrlReservations();
}
