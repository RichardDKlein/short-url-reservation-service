package com.richarddklein.shorturlreservationservice.service;

import java.util.List;

import com.richarddklein.shorturlreservationservice.util.ParameterStoreReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturlreservationservice.dao.ShortUrlReservationDao;
import com.richarddklein.shorturlreservationservice.entity.ShortUrlReservation;

@Service
public class ShortUrlReservationServiceImpl implements ShortUrlReservationService{
    private ParameterStoreReader parameterStoreReader;
    private ShortUrlReservationDao shortUrlReservationDao;

    @Autowired
    public ShortUrlReservationServiceImpl(
            ParameterStoreReader parameterStoreReader,
            ShortUrlReservationDao shortUrlReservationDao) {

        this.parameterStoreReader = parameterStoreReader;
        this.shortUrlReservationDao = shortUrlReservationDao;
    }

    @Override
    public void initializeShortUrlReservationTable() {
        shortUrlReservationDao.initializeShortUrlReservationTable();
    }

    @Override
    public List<ShortUrlReservation> getShortUrlReservationTable() {
        return shortUrlReservationDao.getShortUrlReservationTable();
    }

    @Override
    public String reserveAnyShortUrl() {
        return "Qx3_Ym";
    }

    @Override
    public void reserveSpecificShortUrl(String shortUrl) {
    }

    @Override
    public void cancelShortUrlReservation(String shortUrl) {
    }
}
