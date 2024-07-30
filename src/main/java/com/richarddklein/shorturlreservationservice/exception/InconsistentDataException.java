/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.exception;

public class InconsistentDataException extends Exception {
    public InconsistentDataException() {
        super("Inconsistent data");
    }
}
