package com.sanosysalvos.kafkaadmin.exception;

public class KafkaAdminException extends RuntimeException {
    public KafkaAdminException(String message) {
        super(message);
    }
    public KafkaAdminException(String message, Throwable cause) {
        super(message, cause);
    }
}
