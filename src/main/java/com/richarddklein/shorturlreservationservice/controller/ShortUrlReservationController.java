package com.richarddklein.shorturlreservationservice.controller;

import java.util.Map;

import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shorturl/reservations")
public class ShortUrlReservationController {
    private final ShortUrlReservationService shortUrlReservationService;

    @Autowired
    public ShortUrlReservationController(ShortUrlReservationService shortUrlReservationService) {
        this.shortUrlReservationService = shortUrlReservationService;
    }

    @GetMapping
    public Map<String, String> reserveAnyShortUrl() {
        return shortUrlReservationService.reserveShortUrl();
    }

    @GetMapping("/{shortUrl}")
    public Map<String, String> reserveSpecifiedShortUrl(@PathVariable String shortUrl) {
        return shortUrlReservationService.reserveShortUrl(shortUrl);
    }

    @DeleteMapping("/{shortUrl}")
    public Map<String, String> cancelShortUrlReservation(@PathVariable String shortUrl) {
        return shortUrlReservationService.cancelShortUrlReservation(shortUrl);
    }
}
