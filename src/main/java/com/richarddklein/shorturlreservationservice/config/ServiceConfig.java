/**
 * Description: The Service @Configuration class.
 *
 * <p>Tells Spring how to construct the ShortUrlReservationService class.
 *
 * @author Richard D. Klein
 * @version 1.0
 * @since 2024-03-04
 */

package com.richarddklein.shorturlreservationservice.config;

import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationService;
import com.richarddklein.shorturlreservationservice.service.ShortUrlReservationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {
    @Autowired
    DaoConfig daoConfig;

    @Bean
    public ShortUrlReservationService
    shortUrlReservationService() {
        return new ShortUrlReservationServiceImpl(daoConfig.shortUrlReservationDao());
    }
}
