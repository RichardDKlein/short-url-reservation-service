/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.config;

import com.richarddklein.shorturlcommonlibrary.config.EnvironmentConfig;
import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationService;
import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationServiceImpl;
import org.springframework.context.annotation.Import;

/**
 * The Service @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Service package.</p>
 */
@Configuration
@Import({EnvironmentConfig.class})
public class ServiceConfig {
    @Autowired
    ShortUrlReservationDao shortUrlReservationDao;

    @Autowired
    HostUtils hostUtils;

    @Bean
    public ShortUrlReservationService
    shortUrlReservationService() {
        return new ShortUrlReservationServiceImpl(shortUrlReservationDao, hostUtils);
    }
}
