package com.dregost.moneytransfer.common;

public class InvalidPayloadException extends RuntimeException {
    public InvalidPayloadException(final Throwable throwable) {
        super(throwable);
    }
}
