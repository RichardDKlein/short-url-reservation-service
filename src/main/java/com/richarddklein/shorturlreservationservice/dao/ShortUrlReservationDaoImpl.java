/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.dao;

import java.util.*;

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
import com.richarddklein.shorturlreservationservice.response.ShortUrlReservationStatus;

/**
 * The production implementation of the Short URL Reservation DAO interface.
 *
 * <p>This implementation uses a DynamoDB table, the Short URL Reservation table, to
 * store each Short URL Reservation item.</p>
 *
 * <p>This table must be strongly consistent, since we cannot allow the possibility
 * that two different users might accidentally reserve the same short URL. Therefore,
 * the table cannot be replicated across multiple, geographically dispersed, instances;
 * there can be only one instance of the table.</p>
 *
 * <p>However, DynamoDB will automatically shard (horizontally scale) the table into
 * multiple, disjoint partitions as the access frequency increases, thereby ensuring
 * acceptable throughput regardless of the user load.</p>
 *
 * <p>Each Short URL Reservation item in the table consists of just three attributes:
 * `shortUrl`, `isAvailable`, and `version`.</p>
 *
 * <p>The `shortUrl` attribute of each Short URL Reservation item is the short URL
 * itself. This is a relatively short string that is unique: No two Short URL Reservation
 * items will contain the same `shortUrl` attribute. The `shortUrl` is an integer that has
 * been encoded using true base-64 encoding.</p>
 *
 * <p>Each character of the `shortUrl` string is a digit that can take on one of 64
 * possible values. Furthermore, the digits are weighted according to their position in
 * the `shortUrl` string: The rightmost digit is multiplied by 1, the next digit to the
 * left of it is multiplied by 64, the next digit to the left of that digit is multiplied
 * by (64 * 64), and so on.</p>
 *
 * <p>With this encoding scheme, a 5-character `shortUrl` can take on over 1 billion
 * unique values, a 6-character `shortUrl` can take on almost 69 billion unique values,
 * and a 7-character `shortUrl` can take on almost 4.4 trillion unique values.</p>
 *
 * <p>The 64 characters that compose the allowable values of each base-64 digit are '0'
 * thru '9', 'a' thru 'z', 'A' thru 'Z', and the characters '_' and '-'. All these
 * characters are legal URL characters that have no special meaning.</p>
 *
 * <p>Note that this base-64 encoding scheme is totally different from the Base64 encoding
 * that is used in HTML to encode binary data such as images.</p>
 *
 * <p>The `shortUrl` attribute is the Partition Key for each Short URL Reservation item.
 * Because it has a uniform hash distribution, it can be used to quickly locate the
 * database partition containing the corresponding Short URL Reservation item.</p>
 *
 * <p>The `isAvailable` attribute of each Short URL Reservation item indicates whether
 * the associated `shortUrl` is available. Our first inclination might be to make this
 * a Boolean attribute: A value of `true` would mean that the associated `shortUrl` is
 * available, while a value of `false` would mean that someone has already reserved
 * this `shortUrl`.</p>
 *
 * <p>However, this would lead to very poor performance of one of our most important
 * use cases: Finding an available short URL. If the `isAvailable` attribute were a
 * Boolean, then in order to find an available short URL, DynamoDB would have to scan
 * the entire table until it found an item whose `isAvailable` attribute had a value
 * of `true`. The inefficiency of this operation would be compounded by the fact that
 * DynamoDB might have to search multiple partitions until it found an available short
 * URL.</p>
 *
 * <p>To solve this problem, we use the concept of a DynamoDB "sparse index". We say
 * that the `isAvailable` attribute is present in a Short URL Reservation item if and
 * only if the corresponding `shortUrl` is available. If the `shortUrl` is not available,
 * then the `isAvailable` attribute is completely absent from the item. We then create
 * a Global Secondary Index (GSI), with the `isAvailable` attribute as the Partition
 * Key. Finding an available short URL is then simply a matter of looking up the first
 * item in the `isAvailable` GSI.</p>
 *
 * <p>The only remaining problem is this: What should we use as the value of the
 * `isAvailable` attribute? We cannot use a Boolean, because a Partition Key cannot be a
 * Boolean. To get around this restriction, we could use the Strings "T" and "F" instead
 * of the Boolean values `true` and `false`, but this would introduce another problem.
 * With only two possible values for `isAvailable`, DynamoDB's hashing of `isAvailable`
 * to locate the appropriate partition would basically be worthless. DynamoDB might have
 * to examine many partitions before finding an available short URL.</p>
 *
 * <p>To solve this remaining problem, we need to let `isAvailable` take on many possible
 * values, so that each value will hash efficiently to the appropriate partition. An easy
 * way to accomplish this is to set `isAvailable` to the same value as `shortUrl`. That is,
 * we say that when a short URL is available, then the corresponding `isAvailable` attribute
 * exists, and has a value equal to `shortUrl`. If a short URL is not available, then the
 * corresponding `isAvailable` attribute is completely absent.</p>
 *
 * <p>The `version` attribute of each Short URL Reservation item is a long integer indicating
 * the version # of the Short URL Reservation entity. This attribute is for the exclusive use
 * of DynamoDB; the developer should not read or write it. DynamoDB uses the `version`
 * attribute for what it calls "optimistic locking".</p>
 *
 * <p>In the optimistic locking scheme, the code proceeds with a read-update-write transaction
 * under the assumption that most of the time the item will not be updated by another user
 * between the `read` and `write` operations. In the (hopefully rare) situations where this
 * is not the case, the `write` operation will fail, allowing the code to retry with a new
 * read-update-write transaction.</p>
 *
 * <p>DynamoDB uses the `version` attribute to detect when another user has updated the same
 * item concurrently. Every time the item is written to the database, DynamoDB first checks
 * whether the `version` attribute in the item is the same as the `version` attribute in the
 * database. If so, DynamoDB lets the `write` proceed, and updates the `version` attribute
 * in the database. If not, DynamoDB announces that the `write` has failed.</p>
 *
 * <p>The Short URL Reservation table is fully populated with short URLs, and each short URL
 * is initialized as being available, before the service goes into production.</p>
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

    /**
     * General constructor.
     *
     * @param parameterStoreReader Dependency injection of a class instance that
     *                             is to play the role of reading parameters from
     *                             the Parameter Store component of the AWS Simple
     *                             System Manager (SSM).
     * @param dynamoDbClient Dependency injection of a class instance that is to
     *                       play the role of a DynamoDB Client.
     * @param shortUrlReservationTable Dependency injection of a class instance
     *                                 that is to model the Short URL Reservation
     *                                 table in DynamoDB.
     */
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

    /**
     * Determine whether the Short URL Reservation table currently exists in
     * DynamoDB.
     *
     * @return `true` if the table currently exists, or `false` otherwise.
     */
    private boolean doesTableExist() {
        try {
            shortUrlReservationTable.describeTable();
        } catch (ResourceNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Delete the Short URL Reservation table from DynamoDB.
     */
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

    /**
     * Create the Short URL Reservation table in DynamoDB.
     */
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

    /**
     * Populate the Short URL Reservation table in DynamoDB.
     *
     * Create a Short URL Reservation item for each short URL in the range
     * specified in the Parameter Store, and mark all the items as being
     * available.
     */
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

    /**
     * Convert a long integer to its base-64 representation.
     *
     * @param n The long integer of interest.
     * @return A string that is the base-64 representation of `n`.
     */
    private String longToShortUrl(long n) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(DIGITS.charAt((int) (n % BASE)));
            n /= BASE;
        } while (n > 0);
        return sb.reverse().toString();
    }

    /**
     * Batch insert some Short URL Reservation items.
     *
     * Into the Short URL Reservation table in DynamoDB, perform a
     * batch insert of a list of Short URL Reservation items. Insert
     * the items in batches rather than one at a time in order to
     * improve efficiency.
     *
     * @param shortUrlReservations The list of Short URL Reservation
     *                             items to be batch inserted.
     */
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

    /**
     * Find an available Short URL Reservation item.
     *
     * In the Short URL Reservation table in DynamoDB, find an available
     * Short URL Reservation item. Use the General Secondary Index (GSI)
     * on the `isAvailable` attribute to avoid a time-consuming scan
     * operation.
     *
     * @return An available Short URl Reservation item.
     * @throws NoShortUrlsAvailableException If no short URLs are available,
     * i.e. if they are all reserved.
     */
    private ShortUrlReservation findAvailableShortUrlReservation() throws NoShortUrlsAvailableException {
        while (true) {
            // Get the first item from the `isAvailable-index` GSI.
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

    /**
     * Update a Short URL Reservation item.
     *
     * In the Short URL Reservation table in DynamoDB, update a specified
     * Short URL Reservation item.
     *
     * @param shortUrlReservation The Short URL Reservation item that is
     *                            to be used to update DynamoDB.
     * @return The updated Short URL Reservation item, or `null` if the
     * update failed. (The update can fail if someone else is updating
     * the same item concurrently.)
     */
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
