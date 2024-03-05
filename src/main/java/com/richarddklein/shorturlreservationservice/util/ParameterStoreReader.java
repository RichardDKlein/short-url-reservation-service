package com.richarddklein.shorturlreservationservice.util;

public interface ParameterStoreReader {
    String getShortUrlReservationTableName();
    long getMinShortUrlBase10();
    long getMaxShortUrlBase10();
}
