package com.wproekt.model.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AccountNotVerified extends AuthenticationException {
    public AccountNotVerified() {
        super("Account is not verified. Check your email");
    }
}
