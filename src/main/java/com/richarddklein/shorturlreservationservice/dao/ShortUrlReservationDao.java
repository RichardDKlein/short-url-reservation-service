package com.richarddklein.shorturlreservationservice.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

@Repository
public class ShortUrlReservationDao {
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

    public void initializeShortUrlReservationsTable(long min, long max) {
        List<ShortUrlReservation> shortUrlReservations = new ArrayList<>();

        for (long i = min; i <= max; i++) {
            String shortUrl = longToShortUrl(i);
            ShortUrlReservation shortUrlReservation = new ShortUrlReservation(
                    shortUrl, false);
            shortUrlReservations.add(shortUrlReservation);
        }
        batchInsertReservations(shortUrlReservations);
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    private String longToShortUrl(long n) {
        final String digits = "0123456789" +
                "abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "_-";
        StringBuilder sb = new StringBuilder();

        do {
            sb.append(digits.charAt((int)(n % 64)));
            n /= 36;
        } while (n > 0);

        return sb.reverse().toString();
   }

    private void batchInsertReservations(List<ShortUrlReservation> shortUrlReservations) {
        List<List<ShortUrlReservation>> batches = new ArrayList<>();
        int batchSize = 25;

        for (int i = 0; i < shortUrlReservations.size(); i += batchSize) {
            int end = Math.min(i + batchSize, shortUrlReservations.size());
            batches.add(shortUrlReservations.subList(i, end));
        }

        for (List<ShortUrlReservation> batch : batches) {
            Map<String, List<WriteRequest>> writeRequests = new HashMap<>();
            List<WriteRequest> writeRequestList = new ArrayList<>();

            for (ShortUrlReservation shortUrlReservation : batch) {
                writeRequestList.add(WriteRequest.builder().putRequest(PutRequest.builder()
                        .item(shortUrlReservation.toAttributeValueMap())
                        .build()).build());
            }
            writeRequests.put(shortUrlReservationsTable.tableName(), writeRequestList);

            BatchWriteItemRequest batchWriteItemRequest = BatchWriteItemRequest.builder()
                    .requestItems(writeRequests)
                    .build();
            dynamoDbClient.batchWriteItem(batchWriteItemRequest);
        }
    }
}
