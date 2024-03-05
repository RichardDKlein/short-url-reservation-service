package com.richarddklein.shorturlreservationservice.controller;

import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationArrayResponse;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationResponse;
import com.richarddklein.shorturlreservationservice.response.StatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface ShortUrlReservationController {
    @PostMapping("/all")
    ResponseEntity<StatusResponse>
    initializeShortUrlReservationTable();

    @GetMapping("/all")
    ResponseEntity<StatusAndShortUrlReservationArrayResponse>
    getAllShortUrlReservations();

    @GetMapping("/specific/{shortUrl}")
    ResponseEntity<StatusAndShortUrlReservationResponse>
    getSpecificShortUrlReservation(@PathVariable String shortUrl);

    @PutMapping("/reserve/any")
    ResponseEntity<StatusAndShortUrlReservationResponse>
    reserveAnyShortUrl();

    @PutMapping("/reserve/specific/{shortUrl}")
    ResponseEntity<StatusResponse>
    reserveSpecificShortUrl(@PathVariable String shortUrl);

    @PutMapping("/reserve/all")
    ResponseEntity<StatusResponse>
    reserveAllShortUrls();

    @PutMapping("/cancel/specific/{shortUrl}")
    ResponseEntity<StatusResponse>
    cancelSpecificShortUrlReservation(@PathVariable String shortUrl);

    @PutMapping("/cancel/all")
    ResponseEntity<StatusResponse>
    cancelAllShortUrlReservations();
}
