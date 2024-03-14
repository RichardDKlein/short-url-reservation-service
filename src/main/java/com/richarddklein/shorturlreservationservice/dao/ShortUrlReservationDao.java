/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dao;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlreservationservice.response.ShortUrlReservationStatus;

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
    void initializeShortUrlReservationRepository();

    /**
     * Gets all the Short URL Reservations from the repository.
     *
     * @return A List of all the ShortUrlReservations that exist
     * in the repository.
     */
    List<ShortUrlReservation> getAllShortUrlReservations();

    /**
     * Gets a specific Short URL Reservation from the repository.
     *
     * @param shortUrl The short URL of interest.
     * @return The ShortUrlReservation corresponding to the specified
     * shortUrl, or `null` if that shortUrl could not be found in the
     * database.
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

    /**
     *
     * @param shortUrl
     * @return
     */
    ShortUrlReservationStatus cancelSpecificShortUrlReservation(String shortUrl);

    /**
     *
     */
    void cancelAllShortUrlReservations();
}
