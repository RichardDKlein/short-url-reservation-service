package com.richarddklein.shorturlreservationservice.dao;

public interface ParameterStoreReader {
    String getShortUrlReservationTableName();
    long getMinShortUrlBase10();
    long getMaxShortUrlBase10();
}
