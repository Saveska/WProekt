package com.wproekt.model.exceptions;

public class InvalidRepeatPassword extends RuntimeException {
    public InvalidRepeatPassword() {
        super("Passwords need to match");
    }
}
