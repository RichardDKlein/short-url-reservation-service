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
     *
     * @return
     */
    String getShortUrlReservationTableName();

    /**
     *
     * @return
     */
    long getMinShortUrlBase10();

    /**
     *
     * @return
     */
    long getMaxShortUrlBase10();
}
