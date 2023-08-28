package com.wproekt.model.exceptions;

public class TokenExpired extends RuntimeException{
    public TokenExpired() {
        super("Token has already expired");
    }

}
