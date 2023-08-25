package com.wproekt.repository;

import com.wproekt.model.Card;
import com.wproekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CardRepository extends JpaRepository<Card,Long> {
    @Modifying
    @Query(value = "delete from task_user_cards where cards_id = :id",nativeQuery = true)
    void deleteFromUserCards(Long id);


}
