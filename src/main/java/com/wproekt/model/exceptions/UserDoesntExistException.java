package com.wproekt.model.exceptions;

public class UserDoesntExistException extends RuntimeException {

    public UserDoesntExistException() {
        super("That username or email doesn't exist");
    }
}
