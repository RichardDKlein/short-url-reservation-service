package com.richarddklein.shorturlreservationservice.entity;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@DynamoDbBean
public class ShortUrlReservation {
    private String shortUrl;

    private boolean isReserved;

    public ShortUrlReservation() {
        // Spring requires a default constructor.
    }

    public ShortUrlReservation(String shortUrl, boolean isReserved) {
        this.shortUrl = shortUrl;
        this.isReserved = isReserved;
    }

    public ShortUrlReservation(Map<String, AttributeValue> item) {
        shortUrl = item.get("short_url").s();
        isReserved = item.get("is_reserved").bool();
    }

    @DynamoDbPartitionKey
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    @DynamoDbAttribute("isReserved")
    public boolean getIsReserved() {
        return isReserved;
    }

    public void setIsReserved(boolean isReserved) {
        this.isReserved = isReserved;
    }

    public Map<String, AttributeValue> toAttributeValueMap() {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put("short_url", AttributeValue.builder().s(shortUrl).build());
        attributeValues.put("is_reserved", AttributeValue.builder().bool(isReserved).build());

        return attributeValues;
    }
}
