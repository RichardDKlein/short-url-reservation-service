/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationArrayResponse;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationResponse;
import com.richarddklein.shorturlreservationservice.response.StatusResponse;
import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationService;
import com.richarddklein.shorturlreservationservice.response.ShortUrlReservationStatus;

/**
 * The production implementation of the Short URL Reservation Controller
 * interface.
 */
@RestController
@RequestMapping({"/shorturl/reservations", "/"})
public class ShortUrlReservationControllerImpl implements ShortUrlReservationController {
    private final ShortUrlReservationService shortUrlReservationService;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlReservationControllerImpl(
            ShortUrlReservationService shortUrlReservationService) {

        this.shortUrlReservationService = shortUrlReservationService;
    }

    @Override
    public ResponseEntity<StatusResponse>
    initializeShortUrlReservationTable(HttpServletRequest request) {
        if (isRunningLocally(request.getRemoteAddr())) {
            shortUrlReservationService.initializeShortUrlReservationRepository();
            StatusResponse response = new StatusResponse(
                    ShortUrlReservationStatus.SUCCESS,
                    "Initialization of Short URL Reservation table "
                            + "completed successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            StatusResponse response = new StatusResponse(
                    ShortUrlReservationStatus.NOT_ON_LOCAL_MACHINE,
                    "Initialization of the Short URL Reservation "
                            + "table can be done only when the service is "
                            + "running on your local machine");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<StatusAndShortUrlReservationArrayResponse>
    getAllShortUrlReservations() {
        List<ShortUrlReservation> shortUrlReservations =
                shortUrlReservationService.getAllShortUrlReservations();
        StatusResponse status = new StatusResponse(
                ShortUrlReservationStatus.SUCCESS,
                "Short URL Reservation table successfully retrieved");
        StatusAndShortUrlReservationArrayResponse response =
                new StatusAndShortUrlReservationArrayResponse(status, shortUrlReservations);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<StatusAndShortUrlReservationResponse>
    getSpecificShortUrlReservation(@PathVariable String shortUrl) {
        ShortUrlReservation shortUrlReservation =
                shortUrlReservationService.getSpecificShortUrlReservation(shortUrl);

        HttpStatus httpStatus;
        StatusResponse status;

        if (shortUrlReservation == null) {
            httpStatus = HttpStatus.NOT_FOUND;
            status = new StatusResponse(
                    ShortUrlReservationStatus.SHORT_URL_NOT_FOUND,
                    String.format("Short URL '%s' not found", shortUrl)
            );
            shortUrlReservation = new ShortUrlReservation(
                    shortUrl, "<not found>");
        } else {
            httpStatus = HttpStatus.OK;
            status = new StatusResponse(
                    ShortUrlReservationStatus.SUCCESS,
                    String.format("Short URL '%s' successfully retrieved", shortUrl)
            );
        }
        StatusAndShortUrlReservationResponse response =
                new StatusAndShortUrlReservationResponse(status, shortUrlReservation);

        return new ResponseEntity<>(response, httpStatus);
    }

    @Override
    public ResponseEntity<StatusAndShortUrlReservationResponse>
    reserveAnyShortUrl() {
        HttpStatus httpStatus;
        StatusResponse status;

        ShortUrlReservation shortUrlReservation;
        try {
            shortUrlReservation = shortUrlReservationService.reserveAnyShortUrl();

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
                    ShortUrlReservationStatus.NO_SHORT_URL_IS_AVAILABLE,
                    String.format("No short URLs are available")
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

    @Override
    public ResponseEntity<StatusResponse>
    reserveSpecificShortUrl(@PathVariable String shortUrl) {
        ShortUrlReservationStatus shortUrlReservationStatus =
                shortUrlReservationService.reserveSpecificShortUrl(shortUrl);

        HttpStatus httpStatus;
        StatusResponse response;

        if (shortUrlReservationStatus == ShortUrlReservationStatus.SHORT_URL_NOT_FOUND) {
            httpStatus = HttpStatus.NOT_FOUND;
            response = new StatusResponse(
                    ShortUrlReservationStatus.SHORT_URL_NOT_FOUND,
                    String.format("Short URL '%s' not found", shortUrl)
            );
        } else if (shortUrlReservationStatus ==
                ShortUrlReservationStatus.SHORT_URL_FOUND_BUT_NOT_AVAILABLE) {

            httpStatus = HttpStatus.CONFLICT;
            response = new StatusResponse(
                    ShortUrlReservationStatus.SHORT_URL_FOUND_BUT_NOT_AVAILABLE,
                    String.format("Short URL '%s' was found, but is not available",
                            shortUrl)
            );
        } else {
            httpStatus = HttpStatus.OK;
            response = new StatusResponse(
                    ShortUrlReservationStatus.SUCCESS,
                    String.format("Short URL '%s' successfully reserved", shortUrl)
            );
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    @Override
    public ResponseEntity<StatusResponse>
    reserveAllShortUrls() {
        shortUrlReservationService.reserveAllShortUrls();

        StatusResponse response = new StatusResponse(
                ShortUrlReservationStatus.SUCCESS,
                "All short URL reservations successfully reserved");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<StatusResponse>
    cancelSpecificShortUrlReservation(@PathVariable String shortUrl) {
        ShortUrlReservationStatus shortUrlReservationStatus =
                shortUrlReservationService.cancelSpecificShortUrlReservation(shortUrl);

        HttpStatus httpStatus;
        StatusResponse response;

        if (shortUrlReservationStatus == ShortUrlReservationStatus.SHORT_URL_NOT_FOUND) {

            httpStatus = HttpStatus.NOT_FOUND;
            response = new StatusResponse(
                    ShortUrlReservationStatus.SHORT_URL_NOT_FOUND,
                    String.format("Short URL '%s' not found", shortUrl)
            );
        } else if (shortUrlReservationStatus ==
                ShortUrlReservationStatus.SHORT_URL_FOUND_BUT_NOT_RESERVED) {

            httpStatus = HttpStatus.CONFLICT;
            response = new StatusResponse(
                    ShortUrlReservationStatus.SHORT_URL_FOUND_BUT_NOT_RESERVED,
                    String.format("Short URL '%s' was found, but is not reserved", shortUrl)
            );
        } else {
            httpStatus = HttpStatus.OK;
            response = new StatusResponse(
                    ShortUrlReservationStatus.SUCCESS,
                    String.format("Short URL '%s' reservation successfully canceled", shortUrl)
            );
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    @Override
    public ResponseEntity<StatusResponse>
    cancelAllShortUrlReservations() {
        shortUrlReservationService.cancelAllShortUrlReservations();

        StatusResponse response = new StatusResponse(
                ShortUrlReservationStatus.SUCCESS,
                "All short URL reservations successfully canceled");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    private boolean isRunningLocally(String remoteAddr) {
        return remoteAddr.equals("127.0.0.1");
    }
}
