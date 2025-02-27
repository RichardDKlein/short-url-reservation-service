/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dao;

import com.richarddklein.shorturlcommonlibrary.environment.ParameterStoreAccessor;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.entity.ShortUrlReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * The DAO (Data Access Object) @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the DAO package.</p>
 */
@Configuration
public class DaoConfig {
    @Autowired
    ParameterStoreAccessor parameterStoreAccessor;

    @Bean
    public ShortUrlReservationDao
    shortUrlReservationDao() {
        return new ShortUrlReservationDaoImpl(
                parameterStoreAccessor,
                dynamoDbClient(),
                shortUrlReservationTable()
        );
    }

    @Bean
    public DynamoDbClient
    dynamoDbClient() {
        return DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public DynamoDbAsyncClient
    dynamoDbAsyncClient() {
        return DynamoDbAsyncClient.builder().build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient
    dynamoDbEnhancedAsyncClient() {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(dynamoDbAsyncClient())
                .build();
    }

    @Bean
    public DynamoDbAsyncTable<ShortUrlReservation>
    shortUrlReservationTable() {
        return dynamoDbEnhancedAsyncClient().table(
                parameterStoreAccessor.getShortUrlReservationTableName().block(),
                TableSchema.fromBean(ShortUrlReservation.class));
    }
}
