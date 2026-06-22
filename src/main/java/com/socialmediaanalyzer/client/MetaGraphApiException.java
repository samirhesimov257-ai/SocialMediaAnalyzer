package com.socialmediaanalyzer.client;

public class MetaGraphApiException extends RuntimeException {

    public MetaGraphApiException(String message) {
        super(message);
    }

    public MetaGraphApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
