package com.richarddklein.shorturlreservationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
// The REST Controller will be instantiated via the `ControllerConfig`
// @Configuration class.
@ComponentScan(excludeFilters = @ComponentScan.Filter(RestController.class))
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
