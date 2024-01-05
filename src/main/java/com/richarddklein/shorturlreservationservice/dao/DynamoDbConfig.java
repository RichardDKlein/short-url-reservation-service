package com.richarddklein.shorturlreservationservice.dao;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {
    private static final String SHORT_URL_RESERVATIONS = "short_url_reservations";

    @Bean
    public DynamoDbClient
    dynamoDbClient() {
        return DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient
    dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }

    @Bean
    public DynamoDbTable<ShortUrlReservation>
    shortUrlReservationTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table(SHORT_URL_RESERVATIONS,
                TableSchema.fromBean(ShortUrlReservation.class));
    }
}
