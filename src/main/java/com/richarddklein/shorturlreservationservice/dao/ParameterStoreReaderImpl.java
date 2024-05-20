/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dao;

import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

/**
 * The production implementation of the Parameter Store Reader interface.
 */
@Component
public class ParameterStoreReaderImpl implements ParameterStoreReader {
    private static final String SHORT_URL_RANGE = "/shortUrl/reservations/range";
    private static final String SHORT_URL_RESERVATION_TABLE_NAME = "/shortUrl/reservations/tableName";

    private final SsmClient ssmClient;

    private String shortUrlReservationTableName;
    private Long minShortUrlBase10;
    private Long maxShortUrlBase10;

    /**
     * General constructor.
     *
     * @param ssmClient Dependency injection of a class instance that is to play
     *                  the role of an SSM (Simple Systems Manager) Client.
     */
    public ParameterStoreReaderImpl(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
    }

    @Override
    public String getShortUrlReservationTableName() {
        if (shortUrlReservationTableName == null) {
            shortUrlReservationTableName = getParameter(SHORT_URL_RESERVATION_TABLE_NAME);
        }
        return shortUrlReservationTableName;
    }

    @Override
    public long getMinShortUrlBase10() {
        if (minShortUrlBase10 == null) {
            String shortUrlRange = getParameter(SHORT_URL_RANGE);
            String[] tokens = shortUrlRange.split(",\\s*");
            minShortUrlBase10 = Long.parseLong(tokens[0]);
        }
        return minShortUrlBase10;
    }

    @Override
    public long getMaxShortUrlBase10() {
        if (maxShortUrlBase10 == null) {
            String shortUrlRange = getParameter(SHORT_URL_RANGE);
            String[] tokens = shortUrlRange.split(",\\s*");
            maxShortUrlBase10 = Long.parseLong(tokens[1]);
        }
        return maxShortUrlBase10;
    }

    /**
     * Get a parameter from the Parameter Store.
     *
     * @param parameterName The name of the parameter of interest.
     * @return The value of the parameter in the Parameter Store.
     */
    private String getParameter(String parameterName) {
        GetParameterResponse parameterResponse =
                ssmClient.getParameter(req -> req.name(parameterName));
        return parameterResponse.parameter().value();
    }
}
