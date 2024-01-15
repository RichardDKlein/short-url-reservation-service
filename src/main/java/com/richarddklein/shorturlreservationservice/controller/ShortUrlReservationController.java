package com.richarddklein.shorturlreservationservice.controller;

import java.util.List;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationArrayResponse;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationResponse;
import com.richarddklein.shorturlreservationservice.response.StatusResponse;
import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationService;
import com.richarddklein.shorturlreservationservice.util.ShortUrlReservationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/shorturl/reservations", "/"})
public class ShortUrlReservationController {
    private final ShortUrlReservationService
            shortUrlReservationService;

    @Autowired
    public ShortUrlReservationController(
            ShortUrlReservationService shortUrlReservationService) {

        this.shortUrlReservationService = shortUrlReservationService;
    }

    @PostMapping("/all")
    public ResponseEntity<StatusResponse>
    initializeShortUrlReservationTable() {
        shortUrlReservationService
                .initializeShortUrlReservationRepository();

        StatusResponse response = new StatusResponse(
                ShortUrlReservationStatus.SUCCESS,
                "Short URL Reservation table "
                + "successfully initialized");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<StatusAndShortUrlReservationArrayResponse>
    getAllShortUrlReservations() {
        List<ShortUrlReservation> shortUrlReservations =
                shortUrlReservationService
                        .getAllShortUrlReservations();

        StatusResponse status = new StatusResponse(
                ShortUrlReservationStatus.SUCCESS,
                "Short URL Reservation table "
                + "successfully retrieved");

        StatusAndShortUrlReservationArrayResponse response =
                new StatusAndShortUrlReservationArrayResponse(
                        status, shortUrlReservations);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/specific/{shortUrl}")
    public ResponseEntity<StatusAndShortUrlReservationResponse>
    getSpecificShortUrlReservation(@PathVariable String shortUrl) {
        ShortUrlReservation shortUrlReservation =
                shortUrlReservationService
                        .getSpecificShortUrlReservation(shortUrl);

        HttpStatus httpStatus;
        StatusResponse status;

        if (shortUrlReservation == null) {
            httpStatus = HttpStatus.NOT_FOUND;
            status = new StatusResponse(
                    ShortUrlReservationStatus.SHORT_URL_NOT_FOUND,
                    String.format(
                            "Short URL '%s' not found",
                            shortUrl)
            );
            shortUrlReservation = new ShortUrlReservation(
                    shortUrl,
                    "<not found>");
        } else {
            httpStatus = HttpStatus.OK;
            status = new StatusResponse(
                    ShortUrlReservationStatus.SUCCESS,
                    String.format(
                            "Short URL '%s' "
                            + "successfully retrieved",
                            shortUrl)
            );
        }
        StatusAndShortUrlReservationResponse response =
                new StatusAndShortUrlReservationResponse(status,
                        shortUrlReservation);

        return new ResponseEntity<>(response, httpStatus);
    }

    @PutMapping("/reserve/any")
    public ResponseEntity<StatusAndShortUrlReservationResponse>
    reserveAnyShortUrl() {
        HttpStatus httpStatus;
        StatusResponse status;

        ShortUrlReservation shortUrlReservation;
        try {
            shortUrlReservation = shortUrlReservationService
                    .reserveAnyShortUrl();

            httpStatus = HttpStatus.OK;
            status = new StatusResponse(
                    ShortUrlReservationStatus.SUCCESS,
                    String.format(
                            "Short URL '%s' successfully reserved",
                            shortUrlReservation.getShortUrl())
            );
        } catch (NoShortUrlsAvailableException e) {
            httpStatus = HttpStatus.NOT_FOUND;
            status = new StatusResponse(
                    ShortUrlReservationStatus
                            .NO_SHORT_URL_IS_AVAILABLE,
                    String.format(
                            "No short URLs are available")
            );
            shortUrlReservation = new ShortUrlReservation(
                    "<not found>",
                    "<not found>");
        }

        StatusAndShortUrlReservationResponse response =
                new StatusAndShortUrlReservationResponse(
                        status, shortUrlReservation);

        return new ResponseEntity<>(response, httpStatus);
    }

    @PutMapping("/reserve/specific/{shortUrl}")
    public ResponseEntity<StatusResponse>
    reserveSpecificShortUrl(@PathVariable String shortUrl) {
        ShortUrlReservationStatus shortUrlReservationStatus =
                shortUrlReservationService
                        .reserveSpecificShortUrl(shortUrl);

        HttpStatus httpStatus;
        StatusResponse response;

        if (shortUrlReservationStatus ==
                ShortUrlReservationStatus.SHORT_URL_NOT_FOUND) {

            httpStatus = HttpStatus.NOT_FOUND;
            response = new StatusResponse(
                    ShortUrlReservationStatus.SHORT_URL_NOT_FOUND,
                    String.format(
                            "Short URL '%s' not found",
                            shortUrl)
            );
        } else if (shortUrlReservationStatus ==
                ShortUrlReservationStatus
                        .SHORT_URL_FOUND_BUT_NOT_AVAILABLE) {

            httpStatus = HttpStatus.CONFLICT;
            response = new StatusResponse(
                    ShortUrlReservationStatus
                            .SHORT_URL_FOUND_BUT_NOT_AVAILABLE,
                    String.format(
                            "Short URL '%s' was found, "
                            + "but is not available",
                            shortUrl)
            );
        } else {
            httpStatus = HttpStatus.OK;
            response = new StatusResponse(
                    ShortUrlReservationStatus.SUCCESS,
                    String.format(
                            "Short URL '%s' "
                            + "successfully reserved",
                            shortUrl)
            );
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    @PutMapping("/reserve/all")
    public ResponseEntity<StatusResponse>
    reserveAllShortUrls() {
        shortUrlReservationService.reserveAllShortUrls();

        StatusResponse response = new StatusResponse(
                ShortUrlReservationStatus.SUCCESS,
                "All short URL reservations "
                        + "successfully reserved");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/cancel/specific/{shortUrl}")
    public ResponseEntity<StatusResponse>
    cancelSpecificShortUrlReservation(@PathVariable String shortUrl) {
        ShortUrlReservationStatus shortUrlReservationStatus =
                shortUrlReservationService
                        .cancelSpecificShortUrlReservation(shortUrl);

        HttpStatus httpStatus;
        StatusResponse response;

        if (shortUrlReservationStatus ==
                ShortUrlReservationStatus.SHORT_URL_NOT_FOUND) {

            httpStatus = HttpStatus.NOT_FOUND;
            response = new StatusResponse(
                    ShortUrlReservationStatus.SHORT_URL_NOT_FOUND,
                    String.format(
                            "Short URL '%s' not found",
                            shortUrl)
            );
        } else if (shortUrlReservationStatus ==
                ShortUrlReservationStatus
                        .SHORT_URL_FOUND_BUT_NOT_RESERVED) {

            httpStatus = HttpStatus.CONFLICT;
            response = new StatusResponse(
                    ShortUrlReservationStatus
                            .SHORT_URL_FOUND_BUT_NOT_RESERVED,
                    String.format(
                            "Short URL '%s' was found, "
                            + "but is not reserved",
                            shortUrl)
            );
        } else {
            httpStatus = HttpStatus.OK;
            response = new StatusResponse(
                    ShortUrlReservationStatus.SUCCESS,
                    String.format(
                            "Short URL '%s' "
                            + "successfully canceled",
                            shortUrl)
            );
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    @PutMapping("/cancel/all")
    public ResponseEntity<StatusResponse>
    cancelAllShortUrlReservations() {
        shortUrlReservationService
                .cancelAllShortUrlReservations();

        StatusResponse response = new StatusResponse(
                ShortUrlReservationStatus.SUCCESS,
                "All short URL reservations "
                        + "successfully canceled");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
