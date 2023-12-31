package com.richarddklein.shorturlreservationservice.dao;

import java.util.*;

import com.richarddklein.shorturlreservationservice.config.DynamoDbConfig;
import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Repository
public class ShortUrlReservationDao {
    private static final String DIGITS =
            "0123456789" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "_-";
    private static final int BASE = DIGITS.length();

    private static final int MAX_BATCH_SIZE = 25;
    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbTable<ShortUrlReservation> shortUrlReservationsTable;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    @Autowired
    public ShortUrlReservationDao(
            DynamoDbClient dynamoDbClient,
            DynamoDbTable<ShortUrlReservation> shortUrlReservationsTable) {

        this.dynamoDbClient = dynamoDbClient;
        this.shortUrlReservationsTable = shortUrlReservationsTable;
    }

    public void initializeShortUrlReservationsTable(
            long minShortUrlBase10, long maxShortUrlBase10) {

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

        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(DynamoDbConfig.SHORT_URL_RESERVATIONS)
                .build();

        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);

        for (Map<String, AttributeValue> item : scanResponse.items()) {
            result.add(new ShortUrlReservation(item));
        }

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

        List<List<ShortUrlReservation>> batches = new ArrayList<>();

        // Organize the given ShortUrlReservations into batches.
        for (int i = 0; i < shortUrlReservations.size(); i += MAX_BATCH_SIZE) {
            int end = Math.min(i + MAX_BATCH_SIZE, shortUrlReservations.size());
            batches.add(shortUrlReservations.subList(i, end));
        }

        // For each batch ...
        for (List<ShortUrlReservation> batch : batches) {

            // Add the ShortUrlReservations in that batch to a write request list.
            List<WriteRequest> writeRequestList = new ArrayList<>();
            for (ShortUrlReservation shortUrlReservation : batch) {
                writeRequestList.add(WriteRequest.builder()
                        .putRequest(PutRequest.builder()
                                .item(shortUrlReservation.toAttributeValueMap())
                                .build())
                        .build());
            }

            // Create a BatchWriteItemRequest containing the write request list.
            Map<String, List<WriteRequest>> writeRequests = new HashMap<>();
            writeRequests.put(shortUrlReservationsTable.tableName(), writeRequestList);
            BatchWriteItemRequest batchWriteItemRequest = BatchWriteItemRequest.builder()
                    .requestItems(writeRequests)
                    .build();

            // Tell DynamoDB to perform the BatchWriteItemRequest.
            dynamoDbClient.batchWriteItem(batchWriteItemRequest);
        }
    }
}
