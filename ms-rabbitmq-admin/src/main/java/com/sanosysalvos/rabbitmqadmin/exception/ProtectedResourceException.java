package com.sanosysalvos.rabbitmqadmin.exception;

public class ProtectedResourceException extends RuntimeException {

    public ProtectedResourceException(String message) {
        super(message);
    }
}
