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
                "isAvailable": true/false
            },
            ...
            {
                "shortUrl": "<xyz>",
                "isAvailable": true/false
            },
        ]
    }

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
    public ResponseEntity<StatusResponse> initializeShortUrlReservationTable() {
        shortUrlReservationService.initializeShortUrlReservationTable();
        StatusResponse response = new StatusResponse(
                true,
                "Short URL Reservation table successfully initialized");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<StatusAndShortUrlReservationArrayResponse> getAllShortUrls() {
        List<ShortUrlReservation> shortUrlReservations =
                shortUrlReservationService.getShortUrlReservationTable();
        StatusResponse status = new StatusResponse(
                true,
                "Short URL Reservation table successfully retrieved");
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
        shortUrlReservationService.reserveSpecificShortUrl(shortUrl);
        String shortUrlLong = Long.toString(shortUrlToLong(shortUrl));
        return Map.of(
                "shortUrl", shortUrl,
                "shortUrlLong", shortUrlLong,
                "message", "Short URL successfully reserved"
        );
    }

    private long shortUrlToLong(String shortUrl) {
        final String DIGITS = "0123456789" +
                "abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "_-";
        final int BASE = DIGITS.length();

        long result = 0;
        for (char c : shortUrl.toCharArray()) {
            int digit = DIGITS.indexOf(c);
            result = result * BASE + digit;
        }
        return result;
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
