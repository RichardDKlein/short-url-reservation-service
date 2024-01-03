package com.richarddklein.shorturlreservationservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ShortUrlReservationServiceImpl implements ShortUrlReservationService{
    @Override
    public Map<String, String> reserveShortUrl() {
        Map<String, String> response = new HashMap<>();
        response.put("shortUrl", "Qx3_Ym");
        response.put("message", "Short URL successfully reserved");
        return response;
    }

    @Override
    public Map<String, String> reserveShortUrl(String shortUrl) {
        Map<String, String> response = new HashMap<>();
        response.put("shortUrl", shortUrl);
        response.put("message", "Short URL successfully reserved");
        return response;
    }

    @Override
    public Map<String, String> cancelShortUrlReservation(String shortUrl) {
        Map<String, String> response = new HashMap<>();
        response.put("shortUrl", shortUrl);
        response.put("message", "Reservation successfully canceled");
        return response;
    }
}
