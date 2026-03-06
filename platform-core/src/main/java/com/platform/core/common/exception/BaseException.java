package com.platform.core.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public BaseException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.code = status.name();
    }
}