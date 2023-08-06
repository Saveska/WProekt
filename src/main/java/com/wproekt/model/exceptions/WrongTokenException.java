package com.wproekt.model.exceptions;

public class WrongTokenException extends RuntimeException {
    public WrongTokenException() {
        super("Token already used or doesn't exist");
    }
}
