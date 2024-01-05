package com.richarddklein.shorturlreservationservice.entity;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@DynamoDbBean
public class ShortUrlReservation {
    private String shortUrl;
    private boolean isReserved;

    public ShortUrlReservation() {
        // Spring needs to see a default constructor.
    }

    public ShortUrlReservation(String shortUrl, boolean isReserved) {
        this.shortUrl = shortUrl;
        this.isReserved = isReserved;
    }

    @DynamoDbPartitionKey
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public Map<String, AttributeValue> toAttributeValueMap() {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put("shortUrl", AttributeValue.builder().s(shortUrl).build());
        attributeValues.put("isReserved", AttributeValue.builder().bool(isReserved).build());

        return attributeValues;
    }
}
