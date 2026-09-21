package com.sanosysalvos.usuarios.service.impl;

public class InvalidMicrosoftLinkException extends RuntimeException {

    public InvalidMicrosoftLinkException(String message) {
        super(message);
    }
}