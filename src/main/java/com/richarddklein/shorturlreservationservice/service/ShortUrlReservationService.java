/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.service;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlreservationservice.response.ShortUrlReservationStatus;

/**
 *
 */
public interface ShortUrlReservationService {
    /**
     *
     */
    void initializeShortUrlReservationRepository();

    /**
     *
     * @return
     */
    List<ShortUrlReservation> getAllShortUrlReservations();

    /**
     *
     * @param shortUrl
     * @return
     */
    ShortUrlReservation getSpecificShortUrlReservation(String shortUrl);

    /**
     *
     * @return
     * @throws NoShortUrlsAvailableException
     */
    ShortUrlReservation reserveAnyShortUrl() throws NoShortUrlsAvailableException;

    /**
     *
     * @param shortUrl
     * @return
     */
    ShortUrlReservationStatus reserveSpecificShortUrl(String shortUrl);

    /**
     *
     */
    void reserveAllShortUrls();

    ShortUrlReservationStatus cancelSpecificShortUrlReservation(String shortUrl);

    /**
     *
     */
    void cancelAllShortUrlReservations();
}
