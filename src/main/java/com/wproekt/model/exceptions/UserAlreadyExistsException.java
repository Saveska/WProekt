package com.wproekt.model.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("A user already exists with the same username or E-Mail!");
    }
}
