package com.richarddklein.shorturlreservationservice.dao;

public enum DbInitializationStatus {
    DELETING_EXISTING_SHORT_URL_RESERVATION_TABLE,
    CREATING_NEW_SHORT_URL_RESERVATION_TABLE,
    POPULATING_SHORT_URL_RESERVATION_TABLE,
    SHORT_URL_RESERVATION_TABLE_INITIALIZATION_COMPLETE;

    private static DbInitializationStatus dbInitializationStatus =
            SHORT_URL_RESERVATION_TABLE_INITIALIZATION_COMPLETE;

    public static synchronized DbInitializationStatus
    getDbInitializationStatus() {
        return dbInitializationStatus;
    }

    public static synchronized void
    setDbInitializationStatus(
            DbInitializationStatus dbInitializationStatus) {

        DbInitializationStatus.dbInitializationStatus =
                dbInitializationStatus;
    }
}
