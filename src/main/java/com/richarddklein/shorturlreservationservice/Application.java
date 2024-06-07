/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;

/**
 * The entry point of the Short URL Reservation Service.
 *
 * <p>Implements the `main()` function of the service, which runs the
 * service as a Spring application.</p>
 */
@SpringBootApplication
@ComponentScan(
        basePackages = {"com.richarddklein.shorturl.short-url-common-library"},
        // The REST Controller will be instantiated via the `ControllerConfig`
        // @Configuration class, so we exclude it from the component scan.
        excludeFilters = @ComponentScan.Filter(RestController.class))

public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
