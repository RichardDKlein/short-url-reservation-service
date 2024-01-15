package com.richarddklein.shorturlreservationservice.util;

import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

public class ShortUrlReservationUtils {
    public static boolean isShortUrlReallyAvailable(
            ShortUrlReservation shortUrlReservation) {

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
