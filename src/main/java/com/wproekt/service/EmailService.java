package com.wproekt.service;

public interface EmailService {


    void sendTokenMail(String to, String token, String host, String username);
    void sendResetPassMail(String to, String token, String host, String username);
}
