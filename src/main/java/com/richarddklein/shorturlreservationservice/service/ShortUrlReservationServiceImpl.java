package com.richarddklein.shorturlreservationservice.service;

import java.util.List;

import com.richarddklein.shorturlreservationservice.util.ParameterStoreReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

@Service
public class ShortUrlReservationServiceImpl implements ShortUrlReservationService{
    private final ShortUrlReservationDao shortUrlReservationDao;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    @Autowired
    public ShortUrlReservationServiceImpl(
            ShortUrlReservationDao shortUrlReservationDao) {

        this.shortUrlReservationDao = shortUrlReservationDao;
    }

    @Override
    public void initializeShortUrlReservationRepository() {
        shortUrlReservationDao.createAllShortUrlReservations();
    }

    @Override
    public List<ShortUrlReservation> getAllShortUrlReservations() {
        return shortUrlReservationDao.readAllShortUrlReservations();
    }

    @Override
    public ShortUrlReservation getSpecificShortUrlReservation(
            String shortUrl) {

        return shortUrlReservationDao.readShortUrlReservation(shortUrl);
    }

    @Override
    public String reserveAnyShortUrl() {
        while (true) {
            String availableShortUrl = findAvailableShortUrl();
            ShortUrlReservation item =
                    shortUrlReservationDao
                            .readShortUrlReservation(availableShortUrl);

            if (isShortUrlReallyAvailable(item)) {
                item.setIsAvailable(null);

                item.setVersion(item.getVersion() + 1);

                ShortUrlReservation updatedItem =
                        shortUrlReservationDao
                                .updateShortUrlReservation(item);

                if (wasUpdateSuccessful(item, updatedItem)) {
                    return availableShortUrl; // Reservation successful
                }
            }
        }
    }

    @Override
    public void reserveSpecificShortUrl(String shortUrl) {
    }

    @Override
    public void cancelShortUrlReservation(String shortUrl) {
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    private String findAvailableShortUrl() {
        // Implement logic to query the isAvailable-index GSI and return an available shortUrl
        // ...
        return "hello";
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

    private boolean wasUpdateSuccessful(
            ShortUrlReservation item, ShortUrlReservation updatedItem) {

        if (updatedItem == null) {
            return false;
        }
        if (updatedItem.getIsAvailable() != null) {
            return false;
        }
        return updatedItem.getShortUrl().equals(item.getShortUrl());
    }
}
