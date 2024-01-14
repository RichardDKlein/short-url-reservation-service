package com.richarddklein.shorturlreservationservice.exception;

public class NoShortUrlsAvailableException extends Exception {
    public NoShortUrlsAvailableException() {
        super("No Short URLs are available");
    }
}
