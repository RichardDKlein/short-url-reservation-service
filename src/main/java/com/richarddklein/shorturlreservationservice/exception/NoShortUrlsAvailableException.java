/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.exception;

/**
 *
 */
public class NoShortUrlsAvailableException extends Exception {
    public NoShortUrlsAvailableException() {
        super("No Short URLs are available");
    }
}
