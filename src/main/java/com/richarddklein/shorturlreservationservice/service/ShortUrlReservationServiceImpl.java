package com.richarddklein.shorturlreservationservice.service;

import java.util.List;

import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlreservationservice.util.ShortUrlReservationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

import static com.richarddklein.shorturlreservationservice.util.ShortUrlReservationUtils.isShortUrlReallyAvailable;

@Service
public class ShortUrlReservationServiceImpl implements ShortUrlReservationService {
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
    @Async
    public void initializeShortUrlReservationRepository() {
        shortUrlReservationDao.initializeShortUrlReservationTable();
    }

    @Override
    public List<ShortUrlReservation> getAllShortUrlReservations() {
        return shortUrlReservationDao.readAllShortUrlReservations();
    }

    @Override
    public ShortUrlReservation getSpecificShortUrlReservation(
            String shortUrl) {

        return shortUrlReservationDao
                .readShortUrlReservation(shortUrl);
    }

    @Override
    public ShortUrlReservation reserveAnyShortUrl()
            throws NoShortUrlsAvailableException {

        ShortUrlReservation updatedShortUrlReservation;
        do {
            ShortUrlReservation availableShortUrlReservation =
                    shortUrlReservationDao
                            .findAvailableShortUrlReservation();

            availableShortUrlReservation.setIsAvailable(null);

            updatedShortUrlReservation = shortUrlReservationDao
                    .updateShortUrlReservation(
                            availableShortUrlReservation);

        } while (updatedShortUrlReservation == null);

        return updatedShortUrlReservation;
    }

    @Override
    public ShortUrlReservationStatus reserveSpecificShortUrl(
            String shortUrl) {

        ShortUrlReservation updatedShortUrlReservation;
        do {
            ShortUrlReservation shortUrlReservation =
                    shortUrlReservationDao
                            .readShortUrlReservation(shortUrl);

            if (shortUrlReservation == null) {
                return ShortUrlReservationStatus
                        .SHORT_URL_NOT_FOUND;
            }

            if (!isShortUrlReallyAvailable(shortUrlReservation)) {
                return ShortUrlReservationStatus
                        .SHORT_URL_FOUND_BUT_NOT_AVAILABLE;
            }

            shortUrlReservation.setIsAvailable(null);

            updatedShortUrlReservation = shortUrlReservationDao
                    .updateShortUrlReservation(shortUrlReservation);

        } while (updatedShortUrlReservation == null);

        return ShortUrlReservationStatus.SUCCESS;
    }

    @Override
    public void reserveAllShortUrls() {
        shortUrlReservationDao.reserveAllShortUrls();
    }

    @Override
    public ShortUrlReservationStatus
    cancelSpecificShortUrlReservation(String shortUrl) {
        ShortUrlReservation updatedShortUrlReservation;
        do {
            ShortUrlReservation shortUrlReservation =
                    shortUrlReservationDao
                            .readShortUrlReservation(shortUrl);

            if (shortUrlReservation == null) {
                return ShortUrlReservationStatus
                        .SHORT_URL_NOT_FOUND;
            }

            if (isShortUrlReallyAvailable(shortUrlReservation)) {
                return ShortUrlReservationStatus
                        .SHORT_URL_FOUND_BUT_NOT_RESERVED;
            }

            shortUrlReservation.setIsAvailable(shortUrl);

            updatedShortUrlReservation = shortUrlReservationDao
                    .updateShortUrlReservation(shortUrlReservation);

        } while (updatedShortUrlReservation == null);

        return ShortUrlReservationStatus.SUCCESS;
    }

    @Override
    public void cancelAllShortUrlReservations() {
        shortUrlReservationDao.cancelAllShortUrlReservations();
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
