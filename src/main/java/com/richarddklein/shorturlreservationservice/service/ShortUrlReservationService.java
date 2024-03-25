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
 * The Short URL Reservation Service interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * provides service-layer functionality to the Short URL Reservation
 * Service.</p>
 */
public interface ShortUrlReservationService {
    /**
     * Initialize the Short URL Reservation repository.
     *
     * <p></p>This is a synchronous method. It will return only when the
     * initialization has completed successfully, or has failed.</p>
     */
    void initializeShortUrlReservationRepository();

    /**
     * Get all Short URL Reservations.
     *
     * @return A list of all the Short URL Reservation entities in
     * the repository.
     */
    List<ShortUrlReservation> getAllShortUrlReservations();

    /**
     * Get specific Short URL Reservation.
     *
     * @param shortUrl The short URL of interest.
     * @return The Short URL Reservation entity corresponding to
     * `shortUrl`, or `null` if `shortUrl` could not be found in
     * the repository.
     */
    ShortUrlReservation getSpecificShortUrlReservation(String shortUrl);

    /**
     * Reserve any short URL.
     *
     * <p>Find any available short URL, and reserve it.</p>
     *
     * @return The Short URL Reservation entity of the newly reserved
     * short URL.
     * @throws NoShortUrlsAvailableException If no short URLs are
     * available, i.e. if all short URLs are already reserved.
     */
    ShortUrlReservation reserveAnyShortUrl() throws NoShortUrlsAvailableException;

    /**
     * Reserve specific short URL.
     *
     * @param shortUrl The short URL to be reserved.
     * @return The success/failure status of the reservation operation.
     */
    ShortUrlReservationStatus reserveSpecificShortUrl(String shortUrl);

    /**
     * Reserve all short URLs.
     *
     * <p>Reserve all available short URLs in the repository. (NOTE: This
     * method will never be called in production. It will be called only
     * by test code to test the use case where `reserveAnyShortUrl()` is
     * called when no short URLs are available.)</p>
     */
    void reserveAllShortUrls();

    /**
     * Cancel specific Short URL Reservation.
     *
     * @param shortUrl The short URL whose reservation is to be canceled.
     * @return The success/failure status of the cancellation.
     */
    ShortUrlReservationStatus cancelSpecificShortUrlReservation(String shortUrl);

    /**
     * Cancel all Short URL Reservations.
     *
     * <p>Cancel the existing reservations for all reserved short URLs in
     * the repository. (NOTE: This method will never be called in production.
     * It will be called only by test code to reset the repository to the
     * initial state where all short URLs are available.)</p>
     */
    void cancelAllShortUrlReservations();
}
