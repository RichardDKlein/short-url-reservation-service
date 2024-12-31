/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.service;

import com.richarddklein.shorturlcommonlibrary.security.util.HostUtils;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.ShortUrlReservationStatus;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.Status;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservation;
import com.richarddklein.shorturlcommonlibrary.service.shorturlreservationservice.dto.StatusAndShortUrlReservationArray;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import reactor.core.publisher.Mono;

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
    public ShortUrlReservationStatus
    initializeShortUrlReservationRepository(ServerHttpRequest request) {
        if (!hostUtils.isRunningLocally(request)) {
            return ShortUrlReservationStatus.NOT_ON_LOCAL_MACHINE;
        }
        shortUrlReservationDao.initializeShortUrlReservationRepository();
        return ShortUrlReservationStatus.SUCCESS;
    }

    @Override
    public Mono<StatusAndShortUrlReservation>
    getSpecificShortUrlReservation(String shortUrl) {
        return shortUrlReservationDao.getSpecificShortUrlReservation(shortUrl)
        .map(shortUrlReservation -> new StatusAndShortUrlReservation(
                new Status(ShortUrlReservationStatus.SUCCESS), shortUrlReservation))
        .onErrorResume(e -> Mono.just(new StatusAndShortUrlReservation(
                new Status(ShortUrlReservationStatus.NO_SUCH_SHORT_URL), null)));
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
    public Mono<ShortUrlReservationStatus>
    reserveSpecificShortUrl(String shortUrl) {
        return shortUrlReservationDao.reserveSpecificShortUrl(shortUrl);
    }

    @Override
    public Mono<ShortUrlReservationStatus>
    reserveAllShortUrls() {
        return shortUrlReservationDao.reserveAllShortUrls();
    }

    @Override
    public Mono<ShortUrlReservationStatus>
    cancelSpecificShortUrlReservation(String shortUrl) {
        return shortUrlReservationDao.cancelSpecificShortUrlReservation(shortUrl);
    }

    @Override
    public Mono<ShortUrlReservationStatus>
    cancelAllShortUrlReservations() {
        return shortUrlReservationDao.cancelAllShortUrlReservations();
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
