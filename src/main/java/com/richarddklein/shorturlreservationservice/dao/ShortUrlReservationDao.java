/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dao;

import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservation;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservationArray;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus;
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

    Mono<ShortUrlStatus>
    reserveSpecificShortUrl(String shortUrl);

    Mono<ShortUrlStatus>
    reserveAllShortUrls();

    Mono<ShortUrlStatus>
    cancelSpecificShortUrlReservation(String shortUrl);

    Mono<ShortUrlStatus>
    cancelAllShortUrlReservations();
}
