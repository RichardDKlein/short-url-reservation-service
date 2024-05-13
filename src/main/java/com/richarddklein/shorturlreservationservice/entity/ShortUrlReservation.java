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
     * The Short URL Reservation item attributes. See the
     * `ShortUrlReservationDaoImpl` Javadoc for a detailed
     * description of these attributes.
     */
    private String shortUrl;
    private String isAvailable;
    private Long version;

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
     * Construct a Short URL Reservation entity from parameters specifying
     * the value of the `shortUrl` and `isAvailable` attributes.
     *
     * @param shortUrl The value of the `shortUrl` attribute.
     * @param isAvailable The value of the `isAvailable` attribute.
     */
    public ShortUrlReservation(String shortUrl, String isAvailable) {
        this.shortUrl = shortUrl;
        this.isAvailable = isAvailable;
    }

    @DynamoDbPartitionKey
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    @DynamoDbAttribute("isAvailable")
    @DynamoDbSecondaryPartitionKey(indexNames = "isAvailable-index")
    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    /**
     * Is this Short URL Reservation entity really available?
     *
     * This is a "transient" getter, i.e. a getter-like method that does not
     * correspond to an actual attribute in a Short URL Reservation item.
     *
     * Verify that this Short URL Reservation entity is really available, i.e.
     * that the `isAvailable` attribute exists and has the same value as the
     * `shortUrl` attribute.
     *
     * @return 'true' if this Short URL Reservation entity is really available,
     * 'false' otherwise.
     */
    @Transient
    public boolean isReallyAvailable() {
        return (isAvailable != null) && (isAvailable.equals(shortUrl));
    }

    @DynamoDbVersionAttribute
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Convert to an attribute-value map.
     *
     * Convert this Short URL Reservation entity to an attribute-value Map
     * containing entries that specify the values of the `shortUrl`, `isAvailable`,
     * and `version` attributes.
     *
     * @return the attribute-value Map corresponding to this Short URL Reservation
     * entity.
     */
    public Map<String, AttributeValue> toAttributeValueMap() {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put("shortUrl", AttributeValue.builder().s(shortUrl).build());
        attributeValues.put("isAvailable", AttributeValue.builder().s(isAvailable).build());
        attributeValues.put("version", AttributeValue.builder().n(version.toString()).build());

        return attributeValues;
    }

    @Override
    public String toString() {
        return "ShortUrlReservation{" +
                "shortUrl='" + shortUrl + '\'' +
                ", isAvailable='" + isAvailable + '\'' +
                ", version=" + version +
                '}';
    }
}
