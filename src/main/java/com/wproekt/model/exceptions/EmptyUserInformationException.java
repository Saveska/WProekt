package com.wproekt.model.exceptions;

public class EmptyUserInformationException extends RuntimeException {
    public EmptyUserInformationException() {
        super("Please fill out all of the fields!");
    }
}
