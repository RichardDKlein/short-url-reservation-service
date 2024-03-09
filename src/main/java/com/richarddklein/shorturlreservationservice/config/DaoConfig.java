/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.config;

import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.ssm.SsmClient;

import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDaoImpl;
import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.dao.ParameterStoreReader;
import com.richarddklein.shorturlreservationservice.dao.ParameterStoreReaderImpl;
import org.springframework.context.annotation.Bean;

/**
 * The DAO (Data Access Object) @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the DAO package.</p>
 */
@Configuration
public class DaoConfig {
    @Bean
    public ShortUrlReservationDao
    shortUrlReservationDao() {
        return new ShortUrlReservationDaoImpl(
                parameterStoreReader(),
                dynamoDbClient(),
                shortUrlReservationTable()
        );
    }

    @Bean
    public DynamoDbClient
    dynamoDbClient() {
        return DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider
                        .create())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient
    dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient())
                .build();
    }

    @Bean
    public DynamoDbTable<ShortUrlReservation>
    shortUrlReservationTable() {
        return dynamoDbEnhancedClient().table(
                parameterStoreReader().getShortUrlReservationTableName(),
                TableSchema.fromBean(ShortUrlReservation.class));
    }
    @Bean
    public SsmClient
    ssmClient() {
        return SsmClient.builder().build();
    }

    @Bean
    public ParameterStoreReader
    parameterStoreReader() {
        return new ParameterStoreReaderImpl(ssmClient());
    }
}
