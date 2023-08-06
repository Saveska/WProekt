package com.wproekt.model.exceptions;

public class EmailAlreadyExists extends RuntimeException {

    public EmailAlreadyExists() {
        super("Email Already Exists");
    }
}
