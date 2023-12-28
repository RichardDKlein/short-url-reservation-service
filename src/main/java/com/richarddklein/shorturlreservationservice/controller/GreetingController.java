package com.richarddklein.shorturlreservationservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shorturl/reservation")
public class GreetingController {
    @GetMapping
    public Map<String, String> doGreeting() {
        Map<String, String> greeting = new HashMap<>();
        greeting.put("greeting", "Hello, World!");
        return greeting;
    }
}
