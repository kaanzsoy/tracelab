package com.kaan.tracelab.common.exception;

// proje bulunamazsa

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}