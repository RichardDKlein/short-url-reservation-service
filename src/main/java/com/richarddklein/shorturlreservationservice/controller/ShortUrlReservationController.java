package com.richarddklein.shorturlreservationservice.controller;

import java.util.List;
import java.util.Map;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/shorturl/reservations", "/"})
public class ShortUrlReservationController {
    private final ShortUrlReservationService shortUrlReservationService;

    @Autowired
    public ShortUrlReservationController(ShortUrlReservationService shortUrlReservationService) {
        this.shortUrlReservationService = shortUrlReservationService;
    }

    @PostMapping
    public Map<String, String> initializeShortUrlReservationsTable() {
        shortUrlReservationService.initializeShortUrlReservationsTable();
        return Map.of(
                "message",
                "Short URL Reservations table successfully initialized"
        );
    }

    @GetMapping("/all")
    public List<ShortUrlReservation> getAllShortUrls() {
        return shortUrlReservationService.getShortUrlReservationsTable();
    }

    @GetMapping
    public Map<String, String> reserveAnyShortUrl() {
        String shortUrl = shortUrlReservationService.reserveAnyShortUrl();
        return Map.of(
                "shortUrl", shortUrl,
                "message", "Short URL successfully reserved"
        );
    }

    @GetMapping("/{shortUrl}")
    public Map<String, String> reserveSpecifiedShortUrl(@PathVariable String shortUrl) {
        shortUrlReservationService.reserveSpecifiedShortUrl(shortUrl);
        return Map.of(
                "shortUrl", shortUrl,
                "message", "Short URL successfully reserved"
        );
    }

    @DeleteMapping("/{shortUrl}")
    public Map<String, String> cancelShortUrlReservation(@PathVariable String shortUrl) {
        shortUrlReservationService.cancelShortUrlReservation(shortUrl);
        return Map.of(
                "shortUrl", shortUrl,
                "message", "Reservation successfully canceled"
        );
    }
}
