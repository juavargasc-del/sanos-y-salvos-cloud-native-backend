package com.sanosysalvos.rabbitmqadmin.exception;

public class ResourceConflictException extends RuntimeException {

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
