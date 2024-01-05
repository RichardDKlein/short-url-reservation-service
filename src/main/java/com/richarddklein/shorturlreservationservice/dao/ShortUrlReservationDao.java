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

    @Autowired
    public ShortUrlReservationDao(
            DynamoDbClient dynamoDbClient,
            DynamoDbTable<ShortUrlReservation> shortUrlReservationsTable) {

        this.dynamoDbClient = dynamoDbClient;
        this.shortUrlReservationsTable = shortUrlReservationsTable;
    }

    public void initializeShortUrlReservationsTable(long min, long max) {
        List<ShortUrlReservation> reservations = new ArrayList<>();

        for (long i = min; i <= max; i++) {
            String shortUrl = longToShortUrl(i);
            ShortUrlReservation reservation = new ShortUrlReservation(shortUrl, false);
            reservations.add(reservation);
        }

        // Batch insert reservations into DynamoDB
        batchInsertReservations(reservations);
    }

    public void saveItem(ShortUrlReservation item) {
        shortUrlReservationsTable.putItem(item);
    }

    private String longToShortUrl(long n) {
        return "hi_there";
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

            for (ShortUrlReservation reservation : batch) {
                writeRequestList.add(WriteRequest.builder().putRequest(PutRequest.builder()
                        .item(reservation.toAttributeValueMap())
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
