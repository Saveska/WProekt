package com.wproekt.repository;

import com.wproekt.model.PasswordResetToken;
import com.wproekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    PasswordResetToken getByUserAndToken(User user, String token);
}
