package com.richarddklein.shorturlreservationservice.controller;

import java.util.List;
import java.util.Map;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationArrayResponse;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationResponse;
import com.richarddklein.shorturlreservationservice.response.StatusResponse;
import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationService;
import com.richarddklein.shorturlreservationservice.util.ShortUrlReservationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
GET /shorturl/reservations/specific/{shortUrl}
    {
        "status": {
            "success" : true/false,
            "message" : "blah, blah..."
        },
        "shortUrlReservation": {
                "shortUrl": "<abc>",
                "isAvailable": true/false
        }
    }

PUT /shorturl/reservations/reserve/any
    {
        "status": {
            "success" : true/false,
            "message" : "blah, blah..."
        },
        "shortUrlReservation": {
                "shortUrl": "<abc>",
                "isAvailable": false
        }
    }

PUT /shorturl/reservations/reserve/specific/{shortUrl}
    {
        "status": {
            "success" : true/false,
            "message" : "blah, blah..."
        },
        "shortUrlReservation": {
                "shortUrl": "<abc>",
                "isAvailable": false
        }
    }

PUT /shorturl/reservations/cancel/all
    {
        "status": {
            "success" : true/false,
            "message" : "blah, blah..."
        }
    }

PUT /shorturl/reservations/cancel/specific/{shortUrl}
    {
        "status": {
            "success" : true/false,
            "message" : "blah, blah..."
        },
        "shortUrlReservation": {
                "shortUrl": "<abc>",
                "isAvailable": true
        }
    }
 */
@RestController
@RequestMapping({"/shorturl/reservations", "/"})
public class ShortUrlReservationController {
    private final ShortUrlReservationService shortUrlReservationService;

    @Autowired
    public ShortUrlReservationController(ShortUrlReservationService shortUrlReservationService) {
        this.shortUrlReservationService = shortUrlReservationService;
    }

    @PostMapping("/all")
    public ResponseEntity<StatusResponse>
    initializeShortUrlReservationTable() {
        shortUrlReservationService.initializeShortUrlReservationRepository();
        StatusResponse response = new StatusResponse(
                ShortUrlReservationStatus.SUCCESS,
                "Short URL Reservation table successfully initialized");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all")
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

    @GetMapping("/specific/{shortUrl}")
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
                    shortUrl,
                    "<not found>");
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

    @GetMapping
    public Map<String, String> reserveAnyShortUrl() {
        String shortUrl = shortUrlReservationService.reserveAnyShortUrl();
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
