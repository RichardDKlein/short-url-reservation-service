package com.richarddklein.shorturlreservationservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

@Component
public class ParameterStoreReader {
    private static final String SHORT_URL_RANGE = "/shortUrl/range";
    private static final String SHORT_URL_RESERVATIONS_TABLE_NAME = "/shortUrl/tableName";

    private final SsmClient ssmClient;

    private String shortUrlReservationsTableName;
    private long minShortUrlBase10;
    private long maxShortUrlBase10;

    @Autowired
    public ParameterStoreReader(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
        loadParameters();
    }

    private void loadParameters() {
        String shortUrlRange = getParameter(SHORT_URL_RANGE);
        String[] tokens = shortUrlRange.split(",\\s*");
        minShortUrlBase10 = Long.parseLong(tokens[0]);
        maxShortUrlBase10 = Long.parseLong(tokens[1]);

        shortUrlReservationsTableName =
                getParameter(SHORT_URL_RESERVATIONS_TABLE_NAME);
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

    public String getShortUrlReservationsTableName() {
        return shortUrlReservationsTableName;
    }

    public long getMinShortUrlBase10() {
        return minShortUrlBase10;
    }

    public long getMaxShortUrlBase10() {
        return maxShortUrlBase10;
    }
}
