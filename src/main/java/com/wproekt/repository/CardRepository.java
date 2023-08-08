package com.wproekt.repository;

import com.wproekt.model.Card;
import com.wproekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card,Long> {

}
