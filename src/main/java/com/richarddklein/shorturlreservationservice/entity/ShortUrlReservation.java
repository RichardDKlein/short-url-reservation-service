package com.richarddklein.shorturlreservationservice.entity;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@DynamoDbBean
public class ShortUrlReservation {
    private String shortUrl;
    private String isAvailable;
    private Long version; // for optimistic locking

    public ShortUrlReservation() {
        // Spring requires a default constructor.
    }

    public ShortUrlReservation(String shortUrl, String isAvailable) {
        this.shortUrl = shortUrl;
        this.isAvailable = isAvailable;
    }

    public ShortUrlReservation(Map<String, AttributeValue> item) {
        shortUrl = item.get("shortUrl").s();
        isAvailable = item.get("isAvailable").s();
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

    @DynamoDbVersionAttribute
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

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
