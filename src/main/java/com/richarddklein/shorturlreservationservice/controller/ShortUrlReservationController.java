package com.richarddklein.shorturlreservationservice.controller;

import java.util.List;
import java.util.Map;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.response.StatusAndShortUrlReservationArrayResponse;
import com.richarddklein.shorturlreservationservice.response.StatusResponse;
import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
POST /shorturl/reservations/all
    {
        "status": {
            "success" : true/false,
            "message" : "blah, blah..."
        }
    }

GET /shorturl/reservations/all
    {
        "status": {
            "success" : true/false,
            "message" : "blah, blah..."
        },
        "shortUrlReservations": [
            {
                "shortUrl": "<abc>",
                "isReserved": true/false
            },
            ...
            {
                "shortUrl": "<xyz>",
                "isReserved": true/false
            },
        ]
    }


GET /shorturl/reservations/reserve/any
    {
        "status": {
            "success" : true/false,
            "message" : "blah, blah..."
        },
        "shortUrlReservation": {
                "shortUrl": "<abc>",
                "isReserved": true/false
        }
    }

GET /shorturl/reservations/reserve/specific/{shortUrl+}
    {
        "status": {
            "success" : true/false,
            "message" : "blah, blah..."
        },
        "shortUrlReservation": {
                "shortUrl": "<abc>",
                "isReserved": true/false
        }
    }

PUT /shorturl/reservations/cancel/all
    {
        "status": {
            "success" : true/false,
            "message" : "blah, blah..."
        }
    }

PUT /shorturl/reservations/cancel/specific/{shortUrl+}
    {
        "status": {
            "success" : true/false,
            "message" : "blah, blah..."
        },
        "shortUrlReservation": {
                "shortUrl": "<abc>",
                "isReserved": true/false
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

    @PostMapping
    public ResponseEntity<StatusResponse> initializeShortUrlReservationsTable() {
        shortUrlReservationService.initializeShortUrlReservationsTable();
        StatusResponse response = new StatusResponse(
                true,
                "Short URL Reservations table successfully initialized");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<StatusAndShortUrlReservationArrayResponse> getAllShortUrls() {
        List<ShortUrlReservation> shortUrlReservations =
                shortUrlReservationService.getShortUrlReservationsTable();
        StatusResponse status = new StatusResponse(
                true,
                "Short URL Reservations table successfully retrieved");
        StatusAndShortUrlReservationArrayResponse response =
                new StatusAndShortUrlReservationArrayResponse(status, shortUrlReservations);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
