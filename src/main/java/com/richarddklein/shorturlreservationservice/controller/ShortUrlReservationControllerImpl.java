/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationArrayResponse;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationResponse;
import com.richarddklein.shorturlreservationservice.response.StatusResponse;
import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationService;
import com.richarddklein.shorturlreservationservice.response.ShortUrlReservationStatus;
import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlreservationservice.response.ShortUrlReservationStatus.SUCCESS;

/**
 * The production implementation of the Short URL Reservation Controller
 * interface.
 */
@RestController
@RequestMapping({"/short-url/reservations", "/"})
public class ShortUrlReservationControllerImpl implements ShortUrlReservationController {
    private final ShortUrlReservationService shortUrlReservationService;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     *
     * @param shortUrlReservationService Dependency injection of a class instance
     *                                   that is to play the role of the Short URL
     *                                   Reservation service layer.
     */
    public ShortUrlReservationControllerImpl(
            ShortUrlReservationService shortUrlReservationService) {

        this.shortUrlReservationService = shortUrlReservationService;
    }

    @Override
    public ResponseEntity<StatusResponse>
    initializeShortUrlReservationRepository(ServerHttpRequest request) {
        ShortUrlReservationStatus shortUrlReservationStatus =
                shortUrlReservationService
                        .initializeShortUrlReservationRepository(request);

        HttpStatus httpStatus;
        String message;

        switch (shortUrlReservationStatus) {
            case SUCCESS:
                httpStatus = HttpStatus.OK;
                message = "Initialization of Short URL Reservation table "
                        + "completed successfully";
                break;

            case NOT_ON_LOCAL_MACHINE:
                httpStatus = HttpStatus.FORBIDDEN;
                message = "Initialization of the Short URL Reservation "
                        + "table can be done only when the service is "
                        + "running on your local machine";
                break;

            default:
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
        }

        return new ResponseEntity<>(
                new StatusResponse(shortUrlReservationStatus, message),
                httpStatus);
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlReservationArrayResponse>>
    getAllShortUrlReservations() {
        return shortUrlReservationService.getAllShortUrlReservations()
        .map(statusAndShortUrlReservationArray -> {
            ShortUrlReservationStatus shortUrlUserStatus = statusAndShortUrlReservationArray.getStatus();
            List<ShortUrlReservation> users = statusAndShortUrlReservationArray.getShortUrlReservations();

            HttpStatus httpStatus;
            String message;

            if (Objects.requireNonNull(shortUrlUserStatus) == SUCCESS) {
                httpStatus = HttpStatus.OK;
                message = "All short URL reservations successfully retrieved";
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }

            return new ResponseEntity<>(new StatusAndShortUrlReservationArrayResponse(
                    new StatusResponse(shortUrlUserStatus, message), users),
                    httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlReservationResponse>>
    getSpecificShortUrlReservation(@PathVariable String shortUrl) {
        return shortUrlReservationService.getSpecificShortUrlReservation(shortUrl)
        .map(statusAndShortUrlReservation -> {
            ShortUrlReservationStatus shortUrlReservationStatus =
                    statusAndShortUrlReservation.getStatus();
            ShortUrlReservation shortUrlReservation =
                    statusAndShortUrlReservation.getShortUrlReservation();

            HttpStatus httpStatus;
            String message;

            switch (shortUrlReservationStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = String.format(
                            "Short URL '%s' successfully retrieved", shortUrl);
                    break;
                case MISSING_SHORT_URL:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty short URL must be specified";
                    break;
                case NO_SUCH_SHORT_URL:
                    httpStatus = HttpStatus.NOT_FOUND;
                    message = String.format(
                            "Short URL '%s' not found", shortUrl);
                    break;
                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            }

            return new ResponseEntity<>(new StatusAndShortUrlReservationResponse(
                    new StatusResponse(shortUrlReservationStatus, message),
                    shortUrlReservation), httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlReservationResponse>>
    reserveAnyShortUrl() {
        return shortUrlReservationService.reserveAnyShortUrl()
        .map(statusAndShortUrlReservation -> {
            ShortUrlReservationStatus shortUrlReservationStatus =
                    statusAndShortUrlReservation.getStatus();
            ShortUrlReservation shortUrlReservation =
                    statusAndShortUrlReservation.getShortUrlReservation();

            HttpStatus httpStatus;
            String message;

            switch (shortUrlReservationStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = String.format(
                            "Short URL '%s' successfully reserved",
                            shortUrlReservation.getShortUrl());
                    break;

                case NO_SHORT_URLS_ARE_AVAILABLE:
                    httpStatus = HttpStatus.NOT_FOUND;
                    message = "No short URLs are available";
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            };

            return new ResponseEntity<>(new StatusAndShortUrlReservationResponse(
                    new StatusResponse(shortUrlReservationStatus, message),
                    shortUrlReservation), httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusResponse>>
    reserveSpecificShortUrl(@PathVariable String shortUrl) {
        return shortUrlReservationService.reserveSpecificShortUrl(shortUrl)
        .map(shortUrlReservationStatus -> {

            HttpStatus httpStatus;
            String message;

            switch (shortUrlReservationStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = String.format(
                            "Short URL '%s' successfully reserved",
                            shortUrl);
                    break;

                case NO_SUCH_SHORT_URL:
                    httpStatus = HttpStatus.NOT_FOUND;
                    message = String.format(
                            "Short URL '%s' does not exist",
                            shortUrl);
                    break;

                case SHORT_URL_ALREADY_RESERVED:
                    httpStatus = HttpStatus.CONFLICT;
                    message = String.format(
                            "Short URL '%s' has already been reserved",
                            shortUrl);
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            };

            return new ResponseEntity<>(
                    new StatusResponse(shortUrlReservationStatus, message),
                    httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusResponse>>
    cancelSpecificShortUrlReservation(@PathVariable String shortUrl) {
        return shortUrlReservationService.cancelSpecificShortUrlReservation(shortUrl)
        .map(shortUrlReservationStatus -> {

            HttpStatus httpStatus;
            String message;

            switch (shortUrlReservationStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = String.format(
                            "Short URL '%s' successfully canceled",
                            shortUrl);
                    break;

                case NO_SUCH_SHORT_URL:
                    httpStatus = HttpStatus.NOT_FOUND;
                    message = String.format(
                            "Short URL '%s' does not exist",
                            shortUrl);
                    break;

                case SHORT_URL_NOT_RESERVED:
                    httpStatus = HttpStatus.CONFLICT;
                    message = String.format(
                            "Short URL '%s' cannot be canceled, because it has not been reserved",
                            shortUrl);
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            };

            return new ResponseEntity<>(
                    new StatusResponse(shortUrlReservationStatus, message),
                    httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusResponse>>
    reserveAllShortUrls() {
        return shortUrlReservationService.reserveAllShortUrls()
        .map(shortUrlReservationStatus -> {

            HttpStatus httpStatus;
            String message;

            if (Objects.requireNonNull(shortUrlReservationStatus) == SUCCESS) {
                httpStatus = HttpStatus.OK;
                message = "All short URL reservations successfully reserved";
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }

            return new ResponseEntity<>(
                    new StatusResponse(shortUrlReservationStatus, message),
                    httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusResponse>>
    cancelAllShortUrlReservations() {
        return shortUrlReservationService.cancelAllShortUrlReservations()
        .map(shortUrlReservationStatus -> {

            HttpStatus httpStatus;
            String message;

            if (Objects.requireNonNull(shortUrlReservationStatus) == SUCCESS) {
                httpStatus = HttpStatus.OK;
                message = "All short URL reservations successfully canceled";
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }

            return new ResponseEntity<>(
                    new StatusResponse(shortUrlReservationStatus, message),
                    httpStatus);
        });
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
