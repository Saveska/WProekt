package com.wproekt.service;

import com.wproekt.model.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User register(String username, String password, String repeatPassword, String name, String surname, String email, String host);

    List<Card> getHomePageCards(String username);

    List<Card> getTrashPageCards(String username);

    Note addNoteCard(String username, String title, String Text);

    TaskCard addTaskCard(String username, String title);

    boolean verifyToken(String username, String token);

}
