/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dao;

/**
 * The Parameter Store Reader interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * reads parameters from the Parameter Store component of the AWS Systems
 * Manager.</p>
 */
public interface ParameterStoreReader {
    /**
     * Get the name of the Short URL Reservation table in the DynamoDB
     * database.
     *
     * @return The name of the Short URL Reservation table in the DynamoDB
     * database.
     */
    String getShortUrlReservationTableName();

    /**
     * Get the minimum short URL, in base-10 representation.
     *
     * @return The base-10 representation of the minimum value of the
     * range of short URLs that are to be stored in the Short URL Reservation
     * table in the DynamoDB database.
     */
    long getMinShortUrlBase10();

    /**
     * Get the maximum short URL, in base-10 representation.
     *
     * @return The base-10 representation of the maximum value of the
     * range of short URLs that are to be stored in the Short URL Reservation
     * table in the DynamoDB database.
     */
    long getMaxShortUrlBase10();
}
