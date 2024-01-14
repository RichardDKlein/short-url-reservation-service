package com.richarddklein.shorturlreservationservice.dao;

import java.util.*;

import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlreservationservice.util.ParameterStoreReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

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
@Repository
public class ShortUrlReservationDaoImpl implements ShortUrlReservationDao {
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
    private final DynamoDbTable<ShortUrlReservation> shortUrlReservationTable;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    @Autowired
    public ShortUrlReservationDaoImpl(
            ParameterStoreReader parameterStoreReader,
            DynamoDbClient dynamoDbClient,
            DynamoDbEnhancedClient dynamoDbEnhancedClient,
            DynamoDbTable<ShortUrlReservation> shortUrlReservationTable) {

        this.parameterStoreReader = parameterStoreReader;
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.shortUrlReservationTable = shortUrlReservationTable;
    }

    @Override
    public void createAllShortUrlReservations() {
        if (doesTableExist()) {
            deleteShortUrlReservationTable();
        }
        createShortUrlReservationTable();
        populateShortUrlReservationTable();
    }

    /**
     * Reads all the ShortUrlReservations.
     * @return A List of all the ShortUrlReservations that exist
     * in the repository.
     */
    @Override
    public List<ShortUrlReservation> readAllShortUrlReservations() {
        List<ShortUrlReservation> result = new ArrayList<>();

        shortUrlReservationTable.scan(req -> req.consistentRead(true))
                .items().forEach(result::add);

        return result;
    }

    /**
     * Reads the ShortUrlReservation corresponding to the specified
     * shortUrl.
     * @param shortUrl The shortUrl of interest.
     * @return The ShortUrlReservation corresponding to the specified
     * shortUrl, or `null` if that shortUrl could not be found in the
     * database.
     */
    @Override
    public ShortUrlReservation readShortUrlReservation(String shortUrl) {
        return shortUrlReservationTable.getItem(
                req -> req
                        .key(key -> key.partitionValue(shortUrl))
                        .consistentRead(true));
    }

    @Override
    public ShortUrlReservation updateShortUrlReservation(
            ShortUrlReservation shortUrlReservation) {

        try {
            // If the caller wants to mark the ShortUrlReservation as being
            // reserved, i.e. not available, he should set the `isAvailable`
            // field to `null`. The `.ignoreNulls(true)` mutation in the
            // builder below will then cause DynamoDB to *remove* the
            // `isAvailable` attribute from the item in the database, and
            // to remove it from the `isAvailable-index` GSI as well.
            return shortUrlReservationTable.updateItem(req -> req
                    .item(shortUrlReservation)
                    .ignoreNulls(true));

        } catch (ConditionalCheckFailedException e) {
            // Version check failed. Someone updated the ShortUrlReservation
            // item in the database after we read the item, so the item we
            // just tried to update contains stale data.
            return null;
        }
    }

    @Override
    public ShortUrlReservation findAvailableShortUrlReservation()
            throws NoShortUrlsAvailableException {

        ShortUrlReservation availableShortUrlReservation;

        while (true) {
            // Get the first item from the `isAvailable-index` GSI, and
            // use it to perform a strongly consistent read of the
            // corresponding ShortUrlReservation.
            SdkIterable<Page<ShortUrlReservation>> pagedResult =
                    shortUrlReservationTable.index("isAvailable-index")
                            .scan(req -> req
                                    .limit(1)
                                    // The `consistentRead()` mutation
                                    // applies only to the base table,
                                    // not to the `isAvailable-index` GSI.
                                    .consistentRead(true));

            try {
                availableShortUrlReservation =
                        pagedResult.iterator().next().items().get(0);

                // Since reads from any GSI, such as `isAvailable-index`, are not
                // strongly consistent, we must perform a manual consistency check,
                // to verify that the ShortUrlReservation we obtained from the
                // `isAvailable-index` really is available.
                if (!isShortUrlReallyAvailable(availableShortUrlReservation)) {
                    continue;
                }
                return availableShortUrlReservation;

            } catch (NullPointerException e) {
                throw new NoShortUrlsAvailableException();
            }
        }
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
        DynamoDbWaiter waiter = DynamoDbWaiter.builder()
                .client(dynamoDbClient)
                .build();
        waiter.waitUntilTableNotExists(builder -> builder
                .tableName(parameterStoreReader
                        .getShortUrlReservationTableName())
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
                )
                .build();

        shortUrlReservationTable.createTable(createTableRequest);

        DynamoDbWaiter waiter = DynamoDbWaiter.builder()
                .client(dynamoDbClient)
                .build();
        waiter.waitUntilTableExists(builder -> builder
                .tableName(parameterStoreReader
                        .getShortUrlReservationTableName())
                .build());

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
            ShortUrlReservation shortUrlReservation = new ShortUrlReservation(
                    shortUrl, shortUrl);
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

    private void batchInsertShortUrlReservations(
            List<ShortUrlReservation> shortUrlReservations) {

        long numItems = shortUrlReservations.size();

        for (int i = 0; i < numItems; i += MAX_BATCH_SIZE) {
            WriteBatch.Builder<ShortUrlReservation> writeBatchBuilder =
                    WriteBatch.builder(ShortUrlReservation.class)
                            .mappedTableResource(shortUrlReservationTable);
            for (int j = i; j < Math.min(i + MAX_BATCH_SIZE, numItems); j++) {
                writeBatchBuilder.addPutItem(shortUrlReservations.get(j));
            }
            WriteBatch writeBatch = writeBatchBuilder.build();
            dynamoDbEnhancedClient.batchWriteItem(
                    builder -> builder.writeBatches(writeBatch).build());
        }
    }

    private boolean isShortUrlReallyAvailable(ShortUrlReservation shortUrlReservation) {
        if (shortUrlReservation == null) {
            return false;
        }
        String isAvailable = shortUrlReservation.getIsAvailable();
        if (isAvailable == null) {
            return false;
        }
        String shortUrl = shortUrlReservation.getShortUrl();
        return isAvailable.equals(shortUrl);
    }
}
