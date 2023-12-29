package com.richarddklein.shorturlreservationservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shorturl/reservation")
public class ShortUrlReservationController {
    @GetMapping
    public Map<String, String> reserveShortUrl() {
        Map<String, String> greeting = new HashMap<>();
        greeting.put("greeting", "Hello, World!");
        return greeting;
    }

    @DeleteMapping("/{shortUrl}")
    public String deleteReservation(@PathVariable String shortUrl) {
        return "Reservation for short URL \"" + shortUrl + "\" has been deleted.";
    }
}
