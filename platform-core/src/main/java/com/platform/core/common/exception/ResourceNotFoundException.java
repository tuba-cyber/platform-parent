package com.platform.core.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(
            String.format("%s bulunamadı. Id: %s", resourceName, id),
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND"
        );
    }

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}
