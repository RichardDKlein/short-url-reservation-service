/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dao;

import java.util.*;

import com.richarddklein.shorturlreservationservice.response.ShortUrlReservationStatus;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;

/*
Your approach sounds reasonable, and it addresses the requirement
of finding an available shortUrl without introducing hot spots.
By leveraging a Global Secondary Index (GSI) on the `isAvailable`
attribute with a specific condition (such as the presence or
absence of the attribute), you can efficiently query for available
shortUrls.

Here's a summary of the key points in your approach:

1. Main Table (`ShortUrlReservations`):

- Items have attributes {`shortUrl` (S), `isAvailable` (S)}.
- If the shortUrl is available, the `isAvailable` attribute is
present, and its value is the same as the shortUrl.
- If the shortUrl is not available, the `isAvailable` attribute
is completely absent.

2. Global Secondary Index (isAvailable GSI):
- Created on the `isAvailable` attribute.
- Contains only the shortUrls that are currently available.
- Due to the nature of the data, where the `isAvailable` value
is the same as the `shortUrl`, the GSI will be evenly distributed
over partitions.

3. Finding an Available shortUrl:
- Query the isAvailable GSI to get the first available shortUrl.
- Query the main table (`ShortUrlReservations`) using the obtained
shortUrl to confirm availability and retrieve additional details.
- Remove the `isAvailable` attribute from the shortUrl in the main
table to mark it as no longer available, which, as a side effect,
updates the `isAvailable` GSI.

This approach is designed to distribute the load evenly across
partitions and avoid hot spots. It allows you to efficiently find
available shortUrls and mark them as unavailable, while maintaining
a fast and scalable system.
 */

/**
 * The production implementation of the Short URL Reservation DAO interface.
 */
@Repository
public class ShortUrlReservationDaoImpl implements ShortUrlReservationDao {
    private static final String DIGITS =
            "0123456789" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "_-";
    private static final int BASE = DIGITS.length();

    private static final int MAX_BATCH_SIZE = 25;
    private static final int SCAN_LIMIT = 128;

    private final ParameterStoreReader parameterStoreReader;
    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbTable<ShortUrlReservation> shortUrlReservationTable;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlReservationDaoImpl(
            ParameterStoreReader parameterStoreReader,
            DynamoDbClient dynamoDbClient,
            DynamoDbTable<ShortUrlReservation> shortUrlReservationTable) {
        this.parameterStoreReader = parameterStoreReader;
        this.dynamoDbClient = dynamoDbClient;
        this.shortUrlReservationTable = shortUrlReservationTable;
    }

    @Override
    public void initializeShortUrlReservationRepository() {
        if (doesTableExist()) {
            deleteShortUrlReservationTable();
        }
        createShortUrlReservationTable();
        populateShortUrlReservationTable();
    }

    @Override
    public List<ShortUrlReservation> getAllShortUrlReservations() {
        List<ShortUrlReservation> result = new ArrayList<>();
        shortUrlReservationTable.scan(req -> req.consistentRead(true))
                .items().forEach(result::add);
        return result;
    }

    @Override
    public ShortUrlReservation getSpecificShortUrlReservation(String shortUrl) {
        return shortUrlReservationTable.getItem(req -> req
                .key(key -> key.partitionValue(shortUrl))
                .consistentRead(true));
    }

    @Override
    public ShortUrlReservation reserveAnyShortUrl() throws NoShortUrlsAvailableException {
        ShortUrlReservation updatedShortUrlReservation;
        do {
            ShortUrlReservation availableShortUrlReservation = findAvailableShortUrlReservation();
            availableShortUrlReservation.setIsAvailable(null);
            updatedShortUrlReservation = updateShortUrlReservation(availableShortUrlReservation);
        } while (updatedShortUrlReservation == null);
        return updatedShortUrlReservation;
    }

    @Override
    public ShortUrlReservationStatus reserveSpecificShortUrl(String shortUrl) {
        ShortUrlReservation updatedShortUrlReservation;
        do {
            ShortUrlReservation shortUrlReservation = getSpecificShortUrlReservation(shortUrl);
            if (shortUrlReservation == null) {
                return ShortUrlReservationStatus.SHORT_URL_NOT_FOUND;
            }
            if (!shortUrlReservation.isReallyAvailable()) {
                return ShortUrlReservationStatus.SHORT_URL_FOUND_BUT_NOT_AVAILABLE;
            }
            shortUrlReservation.setIsAvailable(null);
            updatedShortUrlReservation = updateShortUrlReservation(shortUrlReservation);
        } while (updatedShortUrlReservation == null);
        return ShortUrlReservationStatus.SUCCESS;
    }

    @Override
    public void reserveAllShortUrls() {
        SdkIterable<Page<ShortUrlReservation>> pagedResult =
                shortUrlReservationTable.scan(req -> req
                        .limit(SCAN_LIMIT)
                        .filterExpression(Expression.builder()
                                .expression("attribute_exists(isAvailable)")
                                .build())
                );

        for (Page<ShortUrlReservation> page : pagedResult) {
            for (ShortUrlReservation shortUrlReservation : page.items()) {
                shortUrlReservation.setIsAvailable(null);
                // Don't have to check for update failure, since we're in
                // system maintenance mode.
                updateShortUrlReservation(shortUrlReservation);
            }
        }
    }

    @Override
    public void cancelAllShortUrlReservations() {
        SdkIterable<Page<ShortUrlReservation>> pagedResult =
                shortUrlReservationTable.scan(req -> req
                        .limit(SCAN_LIMIT)
                        .filterExpression(Expression.builder()
                                .expression("attribute_not_exists(isAvailable)")
                                .build()));
        for (Page<ShortUrlReservation> page : pagedResult) {
            for (ShortUrlReservation shortUrlReservation : page.items()) {
                shortUrlReservation.setIsAvailable(shortUrlReservation.getShortUrl());
                // Don't have to check for update failure, since we're in
                // system maintenance mode.
                updateShortUrlReservation(shortUrlReservation);
            }
        }
    }

    @Override
    public ShortUrlReservationStatus cancelSpecificShortUrlReservation(String shortUrl) {
        ShortUrlReservation updatedShortUrlReservation;
        do {
            ShortUrlReservation shortUrlReservation = getSpecificShortUrlReservation(shortUrl);
            if (shortUrlReservation == null) {
                return ShortUrlReservationStatus.SHORT_URL_NOT_FOUND;
            }
            if (shortUrlReservation.isReallyAvailable()) {
                return ShortUrlReservationStatus.SHORT_URL_FOUND_BUT_NOT_RESERVED;
            }
            shortUrlReservation.setIsAvailable(shortUrl);
            updatedShortUrlReservation = updateShortUrlReservation(shortUrlReservation);
        } while (updatedShortUrlReservation == null);
        return ShortUrlReservationStatus.SUCCESS;
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    private boolean doesTableExist() {
        try {
            shortUrlReservationTable.describeTable();
        } catch (ResourceNotFoundException e) {
            return false;
        }
        return true;
    }

    private void deleteShortUrlReservationTable() {
        System.out.print("Deleting the Short URL Reservation table ...");
        shortUrlReservationTable.deleteTable();
        DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build();
        waiter.waitUntilTableNotExists(builder -> builder
                .tableName(parameterStoreReader.getShortUrlReservationTableName())
                .build());
        waiter.close();
        System.out.println(" done!");
    }

    private void createShortUrlReservationTable() {
        System.out.print("Creating the Short URL Reservation table ...");
        CreateTableEnhancedRequest createTableRequest = CreateTableEnhancedRequest.builder()
                .globalSecondaryIndices(gsiBuilder -> gsiBuilder
                        .indexName("isAvailable-index")
                        .projection(projectionBuilder -> projectionBuilder
                                .projectionType(ProjectionType.KEYS_ONLY))
                ).build();
        shortUrlReservationTable.createTable(createTableRequest);
        DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build();
        waiter.waitUntilTableExists(builder -> builder
                .tableName(parameterStoreReader.getShortUrlReservationTableName()).build());
        waiter.close();
        System.out.println(" done!");
    }

    private void populateShortUrlReservationTable() {
        System.out.print("Populating the Short URL Reservation table ...");
        List<ShortUrlReservation> shortUrlReservations = new ArrayList<>();
        long minShortUrlBase10 = parameterStoreReader.getMinShortUrlBase10();
        long maxShortUrlBase10 = parameterStoreReader.getMaxShortUrlBase10();
        for (long i = minShortUrlBase10; i <= maxShortUrlBase10; i++) {
            String shortUrl = longToShortUrl(i);
            ShortUrlReservation shortUrlReservation = new ShortUrlReservation(shortUrl, shortUrl);
            shortUrlReservation.setVersion(1L);
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

    private void batchInsertShortUrlReservations(List<ShortUrlReservation> shortUrlReservations) {
        long numItems = shortUrlReservations.size();
        for (int i = 0; i < numItems; i += MAX_BATCH_SIZE) {
            List<WriteRequest> writeRequests = new ArrayList<>();
            for (int j = i; j < Math.min(i + MAX_BATCH_SIZE, numItems); j++) {
                ShortUrlReservation shortUrlReservation = shortUrlReservations.get(j);
                WriteRequest writeRequest = WriteRequest.builder()
                        .putRequest(put -> put.item(shortUrlReservation.toAttributeValueMap()))
                        .build();
                writeRequests.add(writeRequest);
            }
            dynamoDbClient.batchWriteItem(req -> req.requestItems(Collections.singletonMap(
                    parameterStoreReader.getShortUrlReservationTableName(), writeRequests)));
        }
    }

    private ShortUrlReservation findAvailableShortUrlReservation() throws NoShortUrlsAvailableException {
        while (true) {
            // Get the first item from the `isAvailable-index` GSI,
            SdkIterable<Page<ShortUrlReservation>> pagedResult =
                    shortUrlReservationTable.index("isAvailable-index").scan(req -> req.limit(1));
            try {
                ShortUrlReservation gsiItem = pagedResult.iterator().next().items().get(0);
                ShortUrlReservation availableShortUrlReservation =
                        getSpecificShortUrlReservation(gsiItem.getShortUrl());
                // Since reads from any GSI, such as `isAvailable-index`, are not
                // strongly consistent, we should perform a manual consistency check,
                // to verify that the ShortUrlReservation we obtained from the
                // `isAvailable-index` really is available.
                if (!availableShortUrlReservation.isReallyAvailable()) {
                    continue;
                }
                return availableShortUrlReservation;
            } catch (NullPointerException e) {
                throw new NoShortUrlsAvailableException();
            }
        }
    }

    private ShortUrlReservation updateShortUrlReservation(ShortUrlReservation shortUrlReservation) {
        try {
            return shortUrlReservationTable.updateItem(req -> req.item(shortUrlReservation));
        } catch (ConditionalCheckFailedException e) {
            // Version check failed. Someone updated the ShortUrlReservation
            // item in the database after we read the item, so the item we
            // just tried to update contains stale data.
            System.out.println(e.getMessage());
            return null;
        }
    }
}
