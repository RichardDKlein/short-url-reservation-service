/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.entity;

import java.beans.Transient;
import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * The Entity corresponding to an item in the Short URL Reservations
 * table in AWS DynamoDB.
 */
@DynamoDbBean
public class ShortUrlReservation {
    /**
     * The short URL, which is a short string consisting solely of the
     * characters 0-9, A-Z, a-z, and the characters '_' and '-'.
     */
    private String shortUrl;

    /**
     * A string indicating whether the short URL is available, or has
     * already been reserved by someone. If available, the string will
     * have the same value as `shortUrl`. If not available, the string
     * will be null (and in the database will be entirely absent).
     */
    private String isAvailable;

    /**
     * A Long integer indicating the version # of this Short URL
     * Reservation entity. This field is for the exclusive use of
     * DynamoDB; the developer should not read or write it. DynamoDB
     * uses the `version` field for what it calls "optimistic locking".
     *
     * In the optimistic locking scenario, the code proceeds with a
     * read-update-write transaction under the assumption that most of the
     * time, the item will not be updated by another user between the `read`
     * and `write` operations. In the (hopefully rare) situations where this
     * is not the case, the `write` operation will fail, allowing the code
     * to retry with a new read-update-write transaction.
     *
     * DynamoDB uses the `version` field to detect when another user has
     * updated the same item concurrently. Every time the item is written
     * to the database, DynamoDB first checks whether the `version` field
     * in the entity is the same as the `version` field in the database.
     * If so, DynamoDB lets the `write` proceed, and updates the `version`
     * field in the database. If not, DynamoDB announces that the `write`
     * has failed.
     */
    private Long version; // for optimistic locking

    /**
     * Default constructor.
     *
     * This is not used by our code, but Spring requires it.
     */
    public ShortUrlReservation() {
    }

    /**
     * General constructor.
     *
     * @param shortUrl
     * @param isAvailable
     */
    public ShortUrlReservation(String shortUrl, String isAvailable) {
        this.shortUrl = shortUrl;
        this.isAvailable = isAvailable;
    }

    /**
     *
     * @param item
     */
    public ShortUrlReservation(Map<String, AttributeValue> item) {
        shortUrl = item.get("shortUrl").s();
        isAvailable = item.get("isAvailable").s();
    }

    /**
     *
     * @return
     */
    @DynamoDbPartitionKey
    public String getShortUrl() {
        return shortUrl;
    }

    /**
     *
     * @param shortUrl
     */
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    /**
     *
     * @return
     */
    @DynamoDbAttribute("isAvailable")
    @DynamoDbSecondaryPartitionKey(indexNames = "isAvailable-index")
    public String getIsAvailable() {
        return isAvailable;
    }

    /**
     *
     * @param isAvailable
     */
    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    /**
     *
     * @return
     */
    @Transient
    public boolean isReallyAvailable() {
        return (isAvailable != null) && (isAvailable.equals(shortUrl));
    }

    /**
     *
     * @return
     */
    @DynamoDbVersionAttribute
    public Long getVersion() {
        return version;
    }

    /**
     *
     * @param version
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     *
     * @return
     */
    public Map<String, AttributeValue> toAttributeValueMap() {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put("shortUrl", AttributeValue.builder().s(shortUrl).build());
        attributeValues.put("isAvailable", AttributeValue.builder().s(isAvailable).build());
        attributeValues.put("version", AttributeValue.builder().n(version.toString()).build());

        return attributeValues;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "ShortUrlReservation{" +
                "shortUrl='" + shortUrl + '\'' +
                ", isAvailable='" + isAvailable + '\'' +
                ", version=" + version +
                '}';
    }
}
