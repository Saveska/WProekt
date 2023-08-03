package com.wproekt.service;

import com.wproekt.model.Card;
import com.wproekt.model.User;

public interface CardService {
    boolean putCardInBin(User user, Long id);

    Card getCardById(Long id);

    void editImageCard(Long id, String path);
}
