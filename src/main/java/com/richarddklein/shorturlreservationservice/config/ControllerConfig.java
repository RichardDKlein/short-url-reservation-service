/**
 * Description: The Controller @Configuration class.
 *
 * <p>Tells Spring how to construct the ShortUrlReservationController class.
 *
 * @author Richard D. Klein
 * @version 1.0
 * @since 2024-03-04
 */

package com.richarddklein.shorturlreservationservice.config;

import com.richarddklein.shorturlreservationservice.controller.ShortUrlReservationController;
import com.richarddklein.shorturlreservationservice.controller.ShortUrlReservationControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
