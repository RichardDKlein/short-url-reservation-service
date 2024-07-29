/**
 * The Short URL Reservation Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturlreservationservice.service;

import java.util.List;
import java.util.Objects;

import com.richarddklein.shorturlreservationservice.dto.StatusAndShortUrlReservationArray;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;
import com.richarddklein.shorturlreservationservice.exception.NoShortUrlsAvailableException;
import com.richarddklein.shorturlreservationservice.response.ShortUrlReservationStatus;
import reactor.core.publisher.Mono;

/**
 * The production implementation of the Short URL Reservation Service interface.
 */
@Service
public class ShortUrlReservationServiceImpl implements ShortUrlReservationService {
    private final ShortUrlReservationDao shortUrlReservationDao;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     *
     * @param shortUrlReservationDao Dependency injection of a class instance
     *                               that is to play the role of the Short URL
     *                               Reservation Data Access Object (DAO).
     */
    public ShortUrlReservationServiceImpl(ShortUrlReservationDao shortUrlReservationDao) {
        this.shortUrlReservationDao = shortUrlReservationDao;
    }

    // Initialization of the Short URL Reservation repository is performed
    // rarely,and then only by the Admin from a local machine. Therefore,
    // we do not need to use reactive (asynchronous) programming techniques
    // here. Simple synchronous logic will work just fine.
    @Override
    public ShortUrlReservationStatus
    initializeShortUrlReservationRepository(ServerHttpRequest request) {
        if (!isRunningLocally(Objects.requireNonNull(
                request.getRemoteAddress()).getHostString())) {

            return ShortUrlReservationStatus.NOT_ON_LOCAL_MACHINE;
        }

        shortUrlReservationDao.initializeShortUrlReservationRepository();
        return ShortUrlReservationStatus.SUCCESS;
    }

    @Override
    public Mono<StatusAndShortUrlReservationArray>
    getAllShortUrlReservations() {
        return shortUrlReservationDao.getAllShortUrlReservations();
    }

//    @Override
//    public ShortUrlReservation getSpecificShortUrlReservation(String shortUrl) {
//        return shortUrlReservationDao.getSpecificShortUrlReservation(shortUrl);
//    }
//
//    @Override
//    public ShortUrlReservation reserveAnyShortUrl() throws NoShortUrlsAvailableException {
//        return shortUrlReservationDao.reserveAnyShortUrl();
//    }
//
//    @Override
//    public ShortUrlReservationStatus reserveSpecificShortUrl(String shortUrl) {
//        return shortUrlReservationDao.reserveSpecificShortUrl(shortUrl);
//    }
//
//    @Override
//    public void reserveAllShortUrls() {
//        shortUrlReservationDao.reserveAllShortUrls();
//    }
//
//    @Override
//    public ShortUrlReservationStatus cancelSpecificShortUrlReservation(String shortUrl) {
//        return shortUrlReservationDao.cancelSpecificShortUrlReservation(shortUrl);
//    }
//
//    @Override
//    public void cancelAllShortUrlReservations() {
//        shortUrlReservationDao.cancelAllShortUrlReservations();
//    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    /**
     * Is the service running locally?
     *
     * <p>Determine whether the Short URL Reservation Service is running on your
     * local machine, or in the AWS cloud.</p>
     *
     * @param hostString The host that sent the HTTP request.
     * @return 'true' if the service is running locally, 'false' otherwise.
     */
    private boolean isRunningLocally(String hostString) {
        return hostString.contains("localhost");
    }
}
