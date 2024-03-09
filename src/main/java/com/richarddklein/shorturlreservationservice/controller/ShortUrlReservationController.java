/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationArrayResponse;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationResponse;
import com.richarddklein.shorturlreservationservice.response.StatusResponse;

/**
 * The Short URL Reservation Controller interface.
 *
 * <p>Specifies the REST API endpoints for the Short URL Reservation
 * Service.</p>
 */
public interface ShortUrlReservationController {
    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/all")
    ResponseEntity<StatusResponse>
    initializeShortUrlReservationTable(HttpServletRequest request);

    /**
     *
     * @return
     */
    @GetMapping("/all")
    ResponseEntity<StatusAndShortUrlReservationArrayResponse>
    getAllShortUrlReservations();

    /**
     *
     * @param shortUrl
     * @return
     */
    @GetMapping("/specific/{shortUrl}")
    ResponseEntity<StatusAndShortUrlReservationResponse>
    getSpecificShortUrlReservation(@PathVariable String shortUrl);

    /**
     *
     * @return
     */
    @PutMapping("/reserve/any")
    ResponseEntity<StatusAndShortUrlReservationResponse>
    reserveAnyShortUrl();

    /**
     *
     * @param shortUrl
     * @return
     */
    @PutMapping("/reserve/specific/{shortUrl}")
    ResponseEntity<StatusResponse>
    reserveSpecificShortUrl(@PathVariable String shortUrl);

    /**
     *
     * @return
     */
    @PutMapping("/reserve/all")
    ResponseEntity<StatusResponse>
    reserveAllShortUrls();

    /**
     *
     * @param shortUrl
     * @return
     */
    @PutMapping("/cancel/specific/{shortUrl}")
    ResponseEntity<StatusResponse>
    cancelSpecificShortUrlReservation(@PathVariable String shortUrl);

    /**
     *
     * @return
     */
    @PutMapping("/cancel/all")
    ResponseEntity<StatusResponse>
    cancelAllShortUrlReservations();
}
