/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dto;

/**
 * The Short URL Reservation Status.
 *
 * An enumerated type describing the various possible statuses
 * that can be returned in response to a client request.
 */
public enum ShortUrlReservationStatus {
    SUCCESS,
    NO_SHORT_URLS_ARE_AVAILABLE,
    NO_SUCH_SHORT_URL,
    NOT_ON_LOCAL_MACHINE,
    SHORT_URL_ALREADY_RESERVED,
    SHORT_URL_NOT_RESERVED,
    UNKNOWN_ERROR,
}
