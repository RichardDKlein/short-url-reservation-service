/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.service;

import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservation;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservationArray;
import com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus;
import com.richarddklein.shorturlcommonlibrary.service.status.Status;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus.*;

/**
 * The production implementation of the Short URL Reservation Service interface.
 */
@Service
public class ShortUrlReservationServiceImpl implements ShortUrlReservationService {
    private final ShortUrlReservationDao shortUrlReservationDao;
    private final HostUtils hostUtils;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    public ShortUrlReservationServiceImpl(
            ShortUrlReservationDao shortUrlReservationDao,
            HostUtils hostUtils) {

        this.shortUrlReservationDao = shortUrlReservationDao;
        this.hostUtils = hostUtils;
    }

    // Initialization of the Short URL Reservation repository is performed
    // rarely,and then only by the Admin from a local machine. Therefore,
    // we do not need to use reactive (asynchronous) programming techniques
    // here. Simple synchronous logic will work just fine.
    @Override
    public ShortUrlStatus
    initializeShortUrlReservationRepository() {
        if (!hostUtils.isRunningLocally()) {
            return NOT_ON_LOCAL_MACHINE;
        }
        shortUrlReservationDao.initializeShortUrlReservationRepository();
        return SUCCESS;
    }

    @Override
    public Mono<StatusAndShortUrlReservation>
    getSpecificShortUrlReservation(String shortUrl) {
        return shortUrlReservationDao.getSpecificShortUrlReservation(shortUrl)
            .map(shortUrlReservation -> new StatusAndShortUrlReservation(
                    new Status(SUCCESS),
                    shortUrlReservation))
            .onErrorResume(e -> Mono.just(new StatusAndShortUrlReservation(
                    new Status(NO_SUCH_SHORT_URL),
                    null)));
    }

    @Override
    public Mono<StatusAndShortUrlReservationArray>
    getAllShortUrlReservations() {
        return shortUrlReservationDao.getAllShortUrlReservations();
    }

    @Override
    public Mono<StatusAndShortUrlReservation>
    reserveAnyShortUrl() {
        return shortUrlReservationDao.reserveAnyShortUrl();
    }

    @Override
    public Mono<ShortUrlStatus>
    reserveSpecificShortUrl(String shortUrl) {
        return shortUrlReservationDao.reserveSpecificShortUrl(shortUrl);
    }

    @Override
    public Mono<ShortUrlStatus>
    reserveAllShortUrls() {
        return shortUrlReservationDao.reserveAllShortUrls();
    }

    @Override
    public Mono<ShortUrlStatus>
    cancelSpecificShortUrlReservation(String shortUrl) {
        return shortUrlReservationDao.cancelSpecificShortUrlReservation(shortUrl);
    }

    @Override
    public Mono<ShortUrlStatus>
    cancelAllShortUrlReservations() {
        return shortUrlReservationDao.cancelAllShortUrlReservations();
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
