/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationArrayResponse;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationResponse;
import com.richarddklein.shorturlreservationservice.response.StatusResponse;
import reactor.core.publisher.Mono;

/**
 * The Short URL Reservation Controller interface.
 *
 * <p>Specifies the REST API endpoints for the Short URL Reservation
 * Service.</p>
 */
public interface ShortUrlReservationController {
    /**
     * Initialize the Short URL Reservation repository.
     *
     * <p>This is a synchronous operation. It will return a response
     * to the client only when the database initialization has
     * completed successfully, or has failed.</p>
     *
     * <p>Because database initialization is a long-running operation
     * that exceeds the AWS API Gateway maximum response timeout of
     * 30 seconds, this REST endpoint is available only when the Short
     * URL Reservation Service is running on localhost, not on AWS.</p>
     *
     * @param request The HTTP Request.
     * @return An HTTP Response Entity containing the status (success
     * or failure) of the database initialization operation.
     */
    @PostMapping("/initialize-repository")
    ResponseEntity<StatusResponse>
    initializeShortUrlReservationRepository(ServerHttpRequest request);

    /**
     * Get all Short URL Reservation items.
     *
     * <p>Read all the Short URL Reservation items from the Short URL
     * Reservation Table in the database, and return them to the client.</p>
     *
     * @return An HTTP Response Entity containing the status (success
     * or failure) of the operation, as well as an array containing
     * all Short URL Reservation items in the database (if the operation
     * was successful).
     */
    @GetMapping("/all")
    Mono<ResponseEntity<StatusAndShortUrlReservationArrayResponse>>
    getAllShortUrlReservations();

    /**
     * Get a specific Short URL Reservation item.
     *
     * <p>Read the specified Short URL Reservation item from the Short URL
     * Reservation Table in the database, and return it to the client.</p>
     *
     * @param shortUrl A string specifying the short URL of the Short URL
     *                 Reservation item of interest.
     * @return An HTTP Response Entity containing the status (success or
     * failure) of the operation, as well as the desired Short URL Reservation
     * item (if the operation was successful).
     */
    @GetMapping("/specific/{shortUrl}")
    Mono<ResponseEntity<StatusAndShortUrlReservationResponse>>
    getSpecificShortUrlReservation(@PathVariable String shortUrl);

    /**
     * Reserve any available short URL.
     *
     * <p>Find any available Short URL Reservation item in the database,
     * and return its short URL string to the client.</p>
     *
     * @return An HTTP Response Entity containing the status (success or
     * failure) of the operation, as well as the short URL string of an
     * available Short URL Reservation item (if the operation was successful).
     */
    @PatchMapping("/reserve/any")
    Mono<ResponseEntity<StatusAndShortUrlReservationResponse>>
    reserveAnyShortUrl();

    /**
     * Reserve a specific short URL.
     *
     * <p>Find a specific Short URL Reservation item in the database,
     * and reserve it (if it's available).</p>
     *
     * @param shortUrl A string specifying the short URL to reserve.
     * @return An HTTP Response Entity containing the status (success
     * or failure) of the operation.
     */
    @PatchMapping("/reserve/specific/{shortUrl}")
    Mono<ResponseEntity<StatusResponse>>
    reserveSpecificShortUrl(@PathVariable String shortUrl);

    /**
     * Cancel a specific Short URL Reservation.
     *
     * <p>Find the specified Short URL Reservation item in the database
     * (if it exists), and mark it as available (if it is currently
     * reserved).</p>
     *
     * @param shortUrl A string specifying the Short URL Reservation to
     *                 be canceled.
     * @return An HTTP Response Entity containing the status (success or
     * failure) of the operation.
     */
    @PatchMapping("/cancel/specific/{shortUrl}")
    Mono<ResponseEntity<StatusResponse>>
    cancelSpecificShortUrlReservation(@PathVariable String shortUrl);

    /**
     * Reserve ALL Short URL Reservation items in the database.
     *
     * <p>This REST endpoint is not available during normal operation of
     * the Short URL Reservation Service. It is used only during system
     * testing, to test the use case where a client tries to reserve a
     * short URL when none are available.</p>
     *
     * @return An HTTP Response Entity containing the status (success or
     * failure) of the operation.
     */
    @PatchMapping("/reserve/all")
    Mono<ResponseEntity<StatusResponse>>
    reserveAllShortUrls();

    /**
     * Cancel ALL Short URL Reservation items in the database.
     *
     * <p>This REST endpoint is not available during normal operation of
     * the Short URL Reservation Service. It is used only during system
     * testing, to reset the status of all Short URL Reservation items to
     * 'available`.</p>
     *
     * @return An HTTP Response Entity containing the status (success or
     * failure) of the operation.
     */
    @PatchMapping("/cancel/all")
    Mono<ResponseEntity<StatusResponse>>
    cancelAllShortUrlReservations();
}
