package com.richarddklein.shorturlreservationservice.service;

import java.util.Map;

public interface ShortUrlReservationService {
    Map<String, String> reserveShortUrl();
    Map<String, String> reserveShortUrl(String shortUrl);
    Map<String, String> cancelReservation(String shortUrl);
}
