package com.wproekt.model.exceptions;

public class EmailDoesntExist extends RuntimeException{
    public EmailDoesntExist() {
        super("Please check your E-Mail address");
    }
}
