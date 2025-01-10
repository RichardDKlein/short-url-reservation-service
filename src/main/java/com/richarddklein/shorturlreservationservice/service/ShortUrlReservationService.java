/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.service;

import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservation;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservationArray;
import com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus;
import reactor.core.publisher.Mono;

/**
 * The Short URL Reservation Service interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * provides service-layer functionality to the Short URL Reservation
 * Service.</p>
 */
public interface ShortUrlReservationService {
    ShortUrlStatus
    initializeShortUrlReservationRepository();

    Mono<StatusAndShortUrlReservation>
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
