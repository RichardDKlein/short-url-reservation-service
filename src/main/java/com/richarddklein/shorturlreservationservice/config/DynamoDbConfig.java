/**
 * Description: The DynamoDB @Configuration class.
 *
 * <p>Tells Spring how to construct classes that are needed in order to
 * work with AWS DynamoDB, for example `DynamoDbEnhancedClient` and
 * `DynamoDbTable`.
 *
 * @author Richard D. Klein
 * @version 1.0
 * @since 2024-01-08
 */

package com.richarddklein.shorturlreservationservice.config;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.util.ParameterStoreReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {
    private final ParameterStoreReader parameterStoreReader;

    @Autowired
    public DynamoDbConfig(ParameterStoreReader parameterStoreReader) {
        this.parameterStoreReader = parameterStoreReader;
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
    dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<ShortUrlReservation>
    shortUrlReservationTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table(
                parameterStoreReader.getShortUrlReservationTableName(),
                TableSchema.fromBean(ShortUrlReservation.class));
    }
}
