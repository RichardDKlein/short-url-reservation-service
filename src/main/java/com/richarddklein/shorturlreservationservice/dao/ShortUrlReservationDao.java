/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dao;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;

/**
 * The Short URL Reservation DAO (Data Access Object) interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * provides access to the data repository of the Short URL Reservation
 * Service.</p>
 */
public interface ShortUrlReservationDao {
    /**
     *
     */
    void initializeShortUrlReservationTable();

    /**
     *
     * @return
     */
    List<ShortUrlReservation> readAllShortUrlReservations();

    /**
     *
     * @param shortUrl
     * @return
     */
    ShortUrlReservation readShortUrlReservation(String shortUrl);

    /**
     *
     * @param shortUrlReservation
     * @return
     */
    ShortUrlReservation updateShortUrlReservation(ShortUrlReservation shortUrlReservation);

    /**
     *
     * @return
     * @throws NoShortUrlsAvailableException
     */
    ShortUrlReservation findAvailableShortUrlReservation() throws NoShortUrlsAvailableException;

    /**
     *
     */
    void reserveAllShortUrls();

    /**
     *
     */
    void cancelAllShortUrlReservations();
}
