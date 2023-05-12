package com.wproekt.service;

import com.wproekt.model.Card;
import com.wproekt.model.Note;
import com.wproekt.model.TaskCard;
import com.wproekt.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User register(String username, String password, String repeatPassword, String name, String surname, String email);

    List<Card> getCards(String username);

    Note addNoteCard(String username, String title, String Text);

    TaskCard addTaskCard(String username, String title);
}
