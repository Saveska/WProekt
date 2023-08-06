package com.wproekt.model.exceptions;

public class AccountNotVerified extends RuntimeException {
    public AccountNotVerified() {
        super("Account is not verified. Check your email");
    }
}
