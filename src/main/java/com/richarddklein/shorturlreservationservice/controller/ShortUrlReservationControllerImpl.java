/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.controller;

import java.util.Objects;

import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservation;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservationArray;
import com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus;
import com.richarddklein.shorturlcommonlibrary.service.status.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationService;
import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus.SUCCESS;

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
    public ResponseEntity<Status>
    initializeShortUrlReservationRepository() {
        ShortUrlStatus shortUrlReservationStatus = shortUrlReservationService
                .initializeShortUrlReservationRepository();

        HttpStatus httpStatus;
        String message;

        switch (shortUrlReservationStatus) {
            case SUCCESS -> {
                httpStatus = HttpStatus.OK;
                message = "Initialization of Short URL Reservation table "
                        + "completed successfully";
            }
            case NOT_ON_LOCAL_MACHINE -> {
                httpStatus = HttpStatus.FORBIDDEN;
                message = "Initialization of the Short URL Reservation "
                        + "table can be done only when the service is "
                        + "running on your local machine";
            }
            default -> {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }
        }

        return new ResponseEntity<>(
                new Status(shortUrlReservationStatus, message),
                httpStatus);
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlReservation>>
    getSpecificShortUrlReservation(@PathVariable String shortUrl) {
        return shortUrlReservationService.getSpecificShortUrlReservation(shortUrl)
            .map(statusAndShortUrlReservation -> {
                ShortUrlStatus shortUrlReservationStatus =
                        statusAndShortUrlReservation.getStatus().getStatus();

                HttpStatus httpStatus;
                String message;

                switch (shortUrlReservationStatus) {
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = String.format(
                                "Short URL '%s' successfully retrieved", shortUrl);
                    }
                    case NO_SUCH_SHORT_URL -> {
                        httpStatus = HttpStatus.NOT_FOUND;
                        message = String.format(
                                "Short URL '%s' does not exist", shortUrl);
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
                }
                statusAndShortUrlReservation.getStatus().setMessage(message);

                return new ResponseEntity<>(statusAndShortUrlReservation, httpStatus);
            });
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlReservationArray>>
    getAllShortUrlReservations() {
        return shortUrlReservationService.getAllShortUrlReservations()
            .map(statusAndShortUrlReservationArray -> {
                ShortUrlStatus shortUrlUserStatus =
                        statusAndShortUrlReservationArray.getStatus().getStatus();

                HttpStatus httpStatus;
                String message;

                if (Objects.requireNonNull(shortUrlUserStatus) == SUCCESS) {
                    httpStatus = HttpStatus.OK;
                    message = "All short URL reservations successfully retrieved";
                } else {
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                }
                statusAndShortUrlReservationArray.getStatus().setMessage(message);

                return new ResponseEntity<>(statusAndShortUrlReservationArray, httpStatus);
            });
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlReservation>>
    reserveAnyShortUrl() {
        return shortUrlReservationService.reserveAnyShortUrl()
            .map(statusAndShortUrlReservation -> {
                ShortUrlStatus shortUrlReservationStatus =
                        statusAndShortUrlReservation.getStatus().getStatus();

                HttpStatus httpStatus;
                String message;

                switch (shortUrlReservationStatus) {
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = String.format(
                                "Short URL '%s' successfully reserved",
                                statusAndShortUrlReservation
                                        .getShortUrlReservation().getShortUrl());
                    }
                    case NO_SHORT_URLS_ARE_AVAILABLE -> {
                        httpStatus = HttpStatus.NOT_FOUND;
                        message = "No short URLs are available";
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
                }
                statusAndShortUrlReservation.getStatus().setMessage(message);

                return new ResponseEntity<>(statusAndShortUrlReservation, httpStatus);
            });
    }

    @Override
    public Mono<ResponseEntity<Status>>
    reserveSpecificShortUrl(@PathVariable String shortUrl) {
        return shortUrlReservationService.reserveSpecificShortUrl(shortUrl)
            .map(shortUrlReservationStatus -> {
                HttpStatus httpStatus;
                String message;

                switch (shortUrlReservationStatus) {
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = String.format(
                                "Short URL '%s' successfully reserved",
                                shortUrl);
                    }
                    case NO_SUCH_SHORT_URL -> {
                        httpStatus = HttpStatus.NOT_FOUND;
                        message = String.format(
                                "Short URL '%s' does not exist",
                                shortUrl);
                    }
                    case SHORT_URL_ALREADY_TAKEN -> {
                        httpStatus = HttpStatus.CONFLICT;
                        message = String.format(
                                "Short URL '%s' has already been reserved",
                                shortUrl);
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
                }

                return new ResponseEntity<>(
                        new Status(shortUrlReservationStatus, message),
                        httpStatus);
            });
    }

    @Override
    public Mono<ResponseEntity<Status>>
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
                        new Status(shortUrlReservationStatus, message),
                        httpStatus);
            });
    }

    @Override
    public Mono<ResponseEntity<Status>>
    cancelSpecificShortUrlReservation(@PathVariable String shortUrl) {
        return shortUrlReservationService.cancelSpecificShortUrlReservation(shortUrl)
            .map(shortUrlReservationStatus -> {
                HttpStatus httpStatus;
                String message;

                switch (shortUrlReservationStatus) {
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = String.format(
                                "Short URL '%s' successfully canceled",
                                shortUrl);
                    }
                    case NO_SUCH_SHORT_URL -> {
                        httpStatus = HttpStatus.NOT_FOUND;
                        message = String.format(
                                "Short URL '%s' does not exist",
                                shortUrl);
                    }
                    case SHORT_URL_NOT_RESERVED -> {
                        httpStatus = HttpStatus.CONFLICT;
                        message = String.format(
                                "Short URL '%s' cannot be canceled, because it has not been reserved",
                                shortUrl);
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
                }

                return new ResponseEntity<>(
                        new Status(shortUrlReservationStatus, message),
                        httpStatus);
            });
    }

    @Override
    public Mono<ResponseEntity<Status>>
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
                        new Status(shortUrlReservationStatus, message),
                        httpStatus);
            });
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
