/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.exception;

public class NoSuchShortUrlException extends Exception {
    public NoSuchShortUrlException() {
        super("No such short URL");
    }
}
