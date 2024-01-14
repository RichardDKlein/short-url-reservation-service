package com.richarddklein.shorturlreservationservice.service;

import java.util.List;

import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlreservationservice.util.ParameterStoreReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

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
    public ShortUrlReservation reserveAnyShortUrl()
            throws NoShortUrlsAvailableException {

        ShortUrlReservation updatedShortUrlReservation;
        do {
            ShortUrlReservation availableShortUrlReservation =
                    shortUrlReservationDao.findAvailableShortUrlReservation();

            availableShortUrlReservation.setIsAvailable(null);

            updatedShortUrlReservation =
                    shortUrlReservationDao.updateShortUrlReservation(availableShortUrlReservation);

        } while (updatedShortUrlReservation == null);

        return updatedShortUrlReservation;
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
}
