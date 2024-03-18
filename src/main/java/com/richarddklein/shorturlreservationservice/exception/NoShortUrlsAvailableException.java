/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.exception;

/**
 * The "No Short URLs Available" exception.
 *
 * Thrown when a client requests any available short URLs,
 * but none are available.
 */
public class NoShortUrlsAvailableException extends Exception {
    public NoShortUrlsAvailableException() {
        super("No Short URLs are available");
    }
}
