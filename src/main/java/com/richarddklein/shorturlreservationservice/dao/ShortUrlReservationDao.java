package com.richarddklein.shorturlreservationservice.dao;

import java.util.*;

import com.richarddklein.shorturlreservationservice.util.ParameterStoreReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

@Repository
public class ShortUrlReservationDao {
    private static final String DIGITS =
            "0123456789" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "_-";
    private static final int BASE = DIGITS.length();

    private static final int MAX_BATCH_SIZE = 25;

    private final ParameterStoreReader parameterStoreReader;
    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<ShortUrlReservation> shortUrlReservationsTable;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    @Autowired
    public ShortUrlReservationDao(
            ParameterStoreReader parameterStoreReader,
            DynamoDbClient dynamoDbClient,
            DynamoDbEnhancedClient dynamoDbEnhancedClient,
            DynamoDbTable<ShortUrlReservation> shortUrlReservationsTable) {

        this.parameterStoreReader = parameterStoreReader;
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.shortUrlReservationsTable = shortUrlReservationsTable;
    }

    public void initializeShortUrlReservationsTable() {
        if (doesTableExist()) {
            deleteShortUrlReservationsTable();
        }
        createShortUrlReservationsTable();
        populateShortUrlReservationsTable();
    }

    public List<ShortUrlReservation> getShortUrlReservationsTable() {
        List<ShortUrlReservation> result = new ArrayList<>();
        shortUrlReservationsTable.scan().items().forEach(result::add);
        result.sort((x, y) -> x.getShortUrl().compareTo(y.getShortUrl()));
        return result;
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    private boolean doesTableExist() {
        try {
            shortUrlReservationsTable.describeTable();
        } catch (ResourceNotFoundException e) {
            return false;
        }
        return true;
    }

    private void deleteShortUrlReservationsTable() {
        System.out.print("Deleting the Short URL Reservations table ...");
        shortUrlReservationsTable.deleteTable();
        DynamoDbWaiter waiter = DynamoDbWaiter.builder()
                .client(dynamoDbClient)
                .build();
        waiter.waitUntilTableNotExists(builder -> builder
                .tableName(parameterStoreReader
                        .getShortUrlReservationsTableName())
                .build());
        waiter.close();
        System.out.println(" done!");
    }

    private void createShortUrlReservationsTable() {
        System.out.print("Creating the Short URL Reservations table ...");
        shortUrlReservationsTable.createTable();
        DynamoDbWaiter waiter = DynamoDbWaiter.builder()
                .client(dynamoDbClient)
                .build();
        waiter.waitUntilTableExists(builder -> builder
                .tableName(parameterStoreReader
                        .getShortUrlReservationsTableName())
                .build());
        waiter.close();
        System.out.println(" done!");
    }

    private void populateShortUrlReservationsTable() {
        System.out.print("Populating the Short URL Reservations table ...");
        List<ShortUrlReservation> shortUrlReservations = new ArrayList<>();

        long minShortUrlBase10 = parameterStoreReader.getMinShortUrlBase10();
        long maxShortUrlBase10 = parameterStoreReader.getMaxShortUrlBase10();

        for (long i = minShortUrlBase10; i <= maxShortUrlBase10; i++) {
            String shortUrl = longToShortUrl(i);
            ShortUrlReservation shortUrlReservation = new ShortUrlReservation(
                    shortUrl, false);
            shortUrlReservations.add(shortUrlReservation);
        }
        batchInsertShortUrlReservations(shortUrlReservations);
        System.out.println(" done!");
    }

    private String longToShortUrl(long n) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(DIGITS.charAt((int) (n % BASE)));
            n /= BASE;
        } while (n > 0);
        return sb.reverse().toString();
    }

    private long shortUrlToLong(String shortUrl) {
        long result = 0;
        for (char c : shortUrl.toCharArray()) {
            int digit = DIGITS.indexOf(c);
            result = result * BASE + digit;
        }
        return result;
    }

    private void batchInsertShortUrlReservations(
            List<ShortUrlReservation> shortUrlReservations) {

        long numItems = shortUrlReservations.size();

        for (int i = 0; i < numItems; i += MAX_BATCH_SIZE) {
            WriteBatch.Builder<ShortUrlReservation> writeBatchBuilder =
                    WriteBatch.builder(ShortUrlReservation.class)
                            .mappedTableResource(shortUrlReservationsTable);
            for (int j = i; j < Math.min(i + MAX_BATCH_SIZE, numItems); j++) {
                writeBatchBuilder.addPutItem(shortUrlReservations.get(j));
            }
            WriteBatch writeBatch = writeBatchBuilder.build();
            dynamoDbEnhancedClient.batchWriteItem(
                    builder -> builder.writeBatches(writeBatch).build());
        }
    }
}
