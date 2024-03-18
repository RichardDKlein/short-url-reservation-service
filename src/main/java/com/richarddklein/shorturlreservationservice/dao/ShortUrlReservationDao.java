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
     * Initialize the Short URL Reservation repository.
     *
     * Create all the Short URL Reservation items in the repository,
     * and mark them all as being available.
     */
    void initializeShortUrlReservationRepository();

    /**
     * Get all the Short URL Reservations from the repository.
     *
     * @return A List of all the Short URL Reservations that exist
     * in the repository.
     */
    List<ShortUrlReservation> getAllShortUrlReservations();

    /**
     * Get a specific Short URL Reservation from the repository.
     *
     * @param shortUrl The short URL of interest.
     * @return The Short URL Reservation corresponding to `shortUrl`,
     * or `null` if that short URL could not be found in the repository.
     */
    ShortUrlReservation getSpecificShortUrlReservation(String shortUrl);

    /**
     * Reserve any available short URL in the repository.
     *
     * @return The Short URL Reservation that was reserved.
     * @throws NoShortUrlsAvailableException if no short URL is
     * available in the repository.
     */
    ShortUrlReservation reserveAnyShortUrl() throws NoShortUrlsAvailableException;

    /**
     * Reserve a specific short URL in the repository.
     *
     * @param shortUrl The short URL to be reserved.
     * @return A status code describing the success or failure
     * of the reservation operation.
     */
    ShortUrlReservationStatus reserveSpecificShortUrl(String shortUrl);

    /**
     * Reserve all available short URLs in the repository.
     *
     * This method will not be called in production. It will
     * be called only by test code, to test the use case where
     * `reserveAnyShortUrl()` is called but no short URL is
     * available.
     */
    void reserveAllShortUrls();

    /**
     * Cancel a specific Short URL Reservation in the repository.
     *
     * @param shortUrl The short URL to be canceled.
     * @return A status code describing the success or failure of
     * the reservation operation.
     */
    ShortUrlReservationStatus cancelSpecificShortUrlReservation(String shortUrl);

    /**
     * Cancel all reserved Short URL Reservations in the repository.
     *
     * This method will not be called in production. It will be
     * called only by test code, to reset the repository to the
     * initial state, in which all short URLs are available.
     */
    void cancelAllShortUrlReservations();
}
