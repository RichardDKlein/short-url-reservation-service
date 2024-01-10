package com.richarddklein.shorturlreservationservice.dao;

import java.util.*;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import static com.richarddklein.shorturlreservationservice.config.DynamoDbConfig.SHORT_URL_RESERVATIONS;

@Repository
public class ShortUrlReservationDao {
    private static final String DIGITS =
            "0123456789" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "_-";
    private static final int BASE = DIGITS.length();

    private static final int MAX_BATCH_SIZE = 25;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<ShortUrlReservation> shortUrlReservationsTable;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    @Autowired
    public ShortUrlReservationDao(
            DynamoDbEnhancedClient dynamoDbEnhancedClient,
            DynamoDbTable<ShortUrlReservation> shortUrlReservationsTable) {

        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.shortUrlReservationsTable = shortUrlReservationsTable;
    }

    public void initializeShortUrlReservationsTable(
            long minShortUrlBase10, long maxShortUrlBase10) {

        if (doesTableExist()) {
            deleteShortUrlReservationsTable();
        }

        createShortUrlReservationsTable();

        List<ShortUrlReservation> shortUrlReservations = new ArrayList<>();

        for (long i = minShortUrlBase10; i <= maxShortUrlBase10; i++) {
            String shortUrl = longToShortUrl(i);
            ShortUrlReservation shortUrlReservation = new ShortUrlReservation(
                    shortUrl, false);
            shortUrlReservations.add(shortUrlReservation);
        }

        batchInsertShortUrlReservations(shortUrlReservations);
    }

    public List<ShortUrlReservation> getShortUrlReservationsTable() {
        List<ShortUrlReservation> result = new ArrayList<>();
        shortUrlReservationsTable.scan().items().forEach(result::add);
        result.sort((x, y) -> x.getShortUrl().compareTo(y.getShortUrl()));
        return result;
    }
/*
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;

public class DynamoDBScanExample {

    public static void main(String[] args) {
        // Initialize the DynamoDB client
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);

        // Replace 'YourTableName' with your actual table name
        Table table = dynamoDB.getTable("YourTableName");

        int numberOfShortUrls = 100; // Replace with the actual number of short URLs

        // Calculate the current time in milliseconds and compute modulo with the number of short URLs
        long currentTimeInMillis = System.currentTimeMillis();
        String randomShortUrl = base64Encode(currentTimeInMillis % numberOfShortUrls);

        // Specify the ScanSpec with FilterExpression, ExclusiveStartKey, and Limit
        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("isReserved = false")
                .withExclusiveStartKey("shortUrl", randomShortUrl)
                .withMaxResultSize(1); // Limit to only retrieve one item

        try {
            // Perform the scan operation
            Item firstMatchingItem = null;
            Item lastEvaluatedKey = null;

            do {
                if (lastEvaluatedKey != null) {
                    scanSpec.withExclusiveStartKey(lastEvaluatedKey);
                }

                ScanOutcome outcome = table.scan(scanSpec);

                for (Item item : outcome.getItems()) {
                    // Extract the shortUrl from the first matching item
                    String firstMatchingShortUrl = item.getString("shortUrl");
                    System.out.println("First matching shortUrl: " + firstMatchingShortUrl);
                    firstMatchingItem = item;
                    break; // Stop when the first matching item is found
                }

                lastEvaluatedKey = outcome.getLastEvaluatedKey();
            } while (firstMatchingItem == null && lastEvaluatedKey != null);

        } catch (Exception e) {
            System.err.println("Unable to scan the table. Error: " + e.getMessage());
        }
    }

    // Replace this method with your actual Base64 encoding logic
    private static String base64Encode(long value) {
        return Base64.getEncoder().encodeToString(String.valueOf(value).getBytes());
    }
}
 */

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    private boolean doesTableExist() {
        try {
            dynamoDbEnhancedClient.table(SHORT_URL_RESERVATIONS,
                    TableSchema.fromBean(ShortUrlReservation.class))
                    .describeTable();
        } catch (ResourceNotFoundException e) {
            return false;
        }
        return true;
    }

    private void createShortUrlReservationsTable() {
        shortUrlReservationsTable.createTable();
    }
    private void deleteShortUrlReservationsTable() {
        shortUrlReservationsTable.deleteTable();
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

        BatchWriteItemEnhancedRequest.Builder batchWriteItemEnhancedRequestBuilder =
                BatchWriteItemEnhancedRequest.builder();

        long numItems = shortUrlReservations.size();

        for (int i = 0; i < numItems; i += MAX_BATCH_SIZE) {
            WriteBatch.Builder<ShortUrlReservation> writeBatchBuilder =
                    WriteBatch.builder(ShortUrlReservation.class)
                            .mappedTableResource(shortUrlReservationsTable);

            for (int j = i; j < Math.min(i + MAX_BATCH_SIZE, numItems); j++) {
                writeBatchBuilder.addPutItem(shortUrlReservations.get(j));
            }
            WriteBatch writeBatch = writeBatchBuilder.build();

            batchWriteItemEnhancedRequestBuilder.writeBatches(writeBatch);
        }

        dynamoDbEnhancedClient.batchWriteItem(
                batchWriteItemEnhancedRequestBuilder.build());
    }
}
