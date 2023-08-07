package com.wproekt.repository;


import com.wproekt.model.Card;
import com.wproekt.model.Label;
import com.wproekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmailIgnoreCase(String username, String Email);

    boolean existsUserByUsername(String username);

    boolean existsUserByEmailIgnoreCase(String email);

    boolean existsByCardsContains(Card card);

}
