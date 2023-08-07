package com.wproekt.service.impl;

import com.wproekt.model.Card;
import com.wproekt.model.Note;
import com.wproekt.model.User;
import com.wproekt.repository.CardRepository;
import com.wproekt.repository.NoteRepository;
import com.wproekt.repository.UserRepository;
import com.wproekt.service.CardService;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class CardServiceImplementation implements CardService {

    CardRepository cardRepository;
    UserRepository userRepository;
    NoteRepository noteRepository;

    public CardServiceImplementation(CardRepository cardRepository, UserRepository userRepository, NoteRepository noteRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }

    @Override
    public boolean putCardInBin(User user, Long id) {
        if (cardRepository.existsById(id)) {
            Card card = cardRepository.getReferenceById(id);
            if (userRepository.existsByCardsContains(card)) {
                card.setIsInBin(true);
                cardRepository.save(card);


                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public Card getCardById(Long id) {
        return cardRepository.getReferenceById(id);
    }

    @Override
    public void editImageCard(Long id, String path) {
        Card card = cardRepository.getReferenceById(id);
        card.setImageLink(path);
        cardRepository.save(card);

    }

    @Override
    public Card setColor(Long id, Integer red, Integer green, Integer blue) {
        Card card = cardRepository.getReferenceById(id);
        card.setColor(new Color(red, green, blue));
        cardRepository.save(card);

        return card;
    }

    @Override
    public Card editTitleCard(Long id, String text) {
        Card card = cardRepository.getReferenceById(id);
        card.setTitle(text);
        cardRepository.save(card);

        return card;
    }

    @Override
    public Note editTextCard(Long id, String text) {
        Note card = noteRepository.getReferenceById(id);
        card.setText(text);
        cardRepository.save(card);

        return card;
    }
}
