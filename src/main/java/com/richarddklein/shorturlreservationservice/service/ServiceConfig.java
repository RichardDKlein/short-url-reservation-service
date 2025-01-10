/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.service;

import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Service @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Service package.</p>
 */
@Configuration
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
