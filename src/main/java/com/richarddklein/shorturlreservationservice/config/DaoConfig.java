/**
 * Description: The DAO (Data Access Object) @Configuration class.
 *
 * <p>Tells Spring how to construct the class that accesses the
 * database.
 *
 * @author Richard D. Klein
 * @version 1.0
 * @since 2024-03-04
 */

package com.richarddklein.shorturlreservationservice.config;

import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.ssm.SsmClient;

@Configuration
public class DaoConfig {
    @Autowired
    SsmConfig ssmConfig;

    @Autowired
    DbConfig dbConfig;

    @Bean
    public ShortUrlReservationDao
    shortUrlReservationDao() {
        return new ShortUrlReservationDaoImpl(
                ssmConfig.parameterStoreReader(),
                dbConfig.dynamoDbClient(),
                dbConfig.shortUrlReservationTable()
        );
    }
}
