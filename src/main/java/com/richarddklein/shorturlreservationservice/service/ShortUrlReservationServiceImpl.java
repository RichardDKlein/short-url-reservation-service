/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlreservationservice.response.ShortUrlReservationStatus;

/**
 *
 */
@Service
public class ShortUrlReservationServiceImpl implements ShortUrlReservationService {
    private final ShortUrlReservationDao shortUrlReservationDao;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlReservationServiceImpl(ShortUrlReservationDao shortUrlReservationDao) {
        this.shortUrlReservationDao = shortUrlReservationDao;
    }

    @Override
    public void initializeShortUrlReservationRepository() {
        shortUrlReservationDao.initializeShortUrlReservationRepository();
    }

    @Override
    public List<ShortUrlReservation> getAllShortUrlReservations() {
        return shortUrlReservationDao.getAllShortUrlReservations();
    }

    @Override
    public ShortUrlReservation getSpecificShortUrlReservation(String shortUrl) {
        return shortUrlReservationDao.getSpecificShortUrlReservation(shortUrl);
    }

    @Override
    public ShortUrlReservation reserveAnyShortUrl() throws NoShortUrlsAvailableException {
        return shortUrlReservationDao.reserveAnyShortUrl();
    }

    @Override
    public ShortUrlReservationStatus reserveSpecificShortUrl(String shortUrl) {
        return shortUrlReservationDao.reserveSpecificShortUrl(shortUrl);
    }

    @Override
    public void reserveAllShortUrls() {
        shortUrlReservationDao.reserveAllShortUrls();
    }

    @Override
    public ShortUrlReservationStatus cancelSpecificShortUrlReservation(String shortUrl) {
        return shortUrlReservationDao.cancelSpecificShortUrlReservation(shortUrl);
    }

    @Override
    public void cancelAllShortUrlReservations() {
        shortUrlReservationDao.cancelAllShortUrlReservations();
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
