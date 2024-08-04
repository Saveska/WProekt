package com.wproekt.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class PasswordResetToken {

    private static final int EXPIRATION = 5;

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private User user;

    private LocalDateTime expiryDate;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION);
    }
}