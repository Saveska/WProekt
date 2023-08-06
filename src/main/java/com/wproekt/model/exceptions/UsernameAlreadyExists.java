package com.wproekt.model.exceptions;

public class UsernameAlreadyExists extends RuntimeException {

    public UsernameAlreadyExists() {
        super("Username Already Exists");
    }
}
