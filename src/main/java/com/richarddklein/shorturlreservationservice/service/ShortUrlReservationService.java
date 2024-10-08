/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.service;

import com.richarddklein.shorturlreservationservice.dto.StatusAndShortUrlReservation;
import com.richarddklein.shorturlreservationservice.dto.StatusAndShortUrlReservationArray;
import com.richarddklein.shorturlreservationservice.dto.ShortUrlReservationStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

/**
 * The Short URL Reservation Service interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * provides service-layer functionality to the Short URL Reservation
 * Service.</p>
 */
public interface ShortUrlReservationService {
    ShortUrlReservationStatus
    initializeShortUrlReservationRepository(ServerHttpRequest request);

    Mono<StatusAndShortUrlReservation>
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
