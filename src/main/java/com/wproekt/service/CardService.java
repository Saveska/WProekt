package com.wproekt.service;

import com.wproekt.model.Card;
import com.wproekt.model.Label;
import com.wproekt.model.Note;
import com.wproekt.model.User;

import java.util.List;

public interface CardService {
    boolean putCardInArchive(User user, Long id);
    boolean putCardInBin(User user, Long id);

    Card getCardById(Long id);

    void editImageCard(Long id, String path);

    Card setColor(Long id, Integer red, Integer green, Integer blue);

    Card editTitleCard(Long id, String text);

    Note editTextCard(Long id, String text);

    Label addLabel(String username, Long cardId, Long labelId);

    Card removeLabel(String username, Long cardId, Long labelId);

    Card togglePin(Long cardId);

    List<Card> reorderUsersCard(String username, List<Object> cardIds);

}
