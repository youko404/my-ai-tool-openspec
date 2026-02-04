package com.ai.content.exception;

import lombok.Getter;

@Getter
public class ProviderRequestException extends IllegalArgumentException {

    private final Integer statusCode;
    private final String responseBody;

    public ProviderRequestException(String message, Integer statusCode, String responseBody, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}
