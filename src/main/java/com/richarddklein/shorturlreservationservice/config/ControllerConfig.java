/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.config;

import com.richarddklein.shorturlreservationservice.controller.ShortUrlReservationController;
import com.richarddklein.shorturlreservationservice.controller.ShortUrlReservationControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Controller @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Controller package.</p>
 */
@Configuration
public class ControllerConfig {
    @Autowired
    ServiceConfig serviceConfig;

    @Bean
    public ShortUrlReservationController
    shortUrlReservationController() {
        return new ShortUrlReservationControllerImpl(serviceConfig.shortUrlReservationService());
    }
}
