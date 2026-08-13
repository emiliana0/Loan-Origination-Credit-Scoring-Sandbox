package com.creditscoring.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Вече съществува потребител с email: " + email);
    }
}
