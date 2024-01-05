package com.richarddklein.shorturlreservationservice.service;

import java.util.HashMap;
import java.util.Map;

import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShortUrlReservationServiceImpl implements ShortUrlReservationService{
    private ShortUrlReservationDao shortUrlReservationDao;

    @Autowired
    public ShortUrlReservationServiceImpl(ShortUrlReservationDao shortUrlReservationDao) {
        this.shortUrlReservationDao = shortUrlReservationDao;
    }

    @Override
    public void initializeShortUrlReservationsTable() {
        shortUrlReservationDao.initializeShortUrlReservationsTable(0, 127);
    }

    @Override
    public String reserveAnyShortUrl() {
        return "Qx3_Ym";
    }

    @Override
    public void reserveSpecifiedShortUrl(String shortUrl) {
    }

    @Override
    public void cancelShortUrlReservation(String shortUrl) {
    }
}
