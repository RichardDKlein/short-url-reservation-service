/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dao;

import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

/**
 * The production implementation of the Parameter Store Reader interface.
 */
@Component
public class ParameterStoreReaderImpl implements ParameterStoreReader {
    private static final String SHORT_URL_RANGE = "/shortUrl/range";
    private static final String SHORT_URL_RESERVATION_TABLE_NAME = "/shortUrl/tableName";

    private final SsmClient ssmClient;

    private String shortUrlReservationTableName;
    private long minShortUrlBase10;
    private long maxShortUrlBase10;

    public ParameterStoreReaderImpl(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
        loadParameters();
    }

    @Override
    public String getShortUrlReservationTableName() {
        return shortUrlReservationTableName;
    }

    @Override
    public long getMinShortUrlBase10() {
        return minShortUrlBase10;
    }

    @Override
    public long getMaxShortUrlBase10() {
        return maxShortUrlBase10;
    }

    private void loadParameters() {
        String shortUrlRange = getParameter(SHORT_URL_RANGE);
        String[] tokens = shortUrlRange.split(",\\s*");
        minShortUrlBase10 = Long.parseLong(tokens[0]);
        maxShortUrlBase10 = Long.parseLong(tokens[1]);

        shortUrlReservationTableName =
                getParameter(SHORT_URL_RESERVATION_TABLE_NAME);
    }

    private String getParameter(String parameterName) {
        GetParameterRequest parameterRequest =
                GetParameterRequest.builder()
                        .name(parameterName)
                        .build();

        GetParameterResponse parameterResponse =
                ssmClient.getParameter(parameterRequest);

        return parameterResponse.parameter().value();
    }
}
