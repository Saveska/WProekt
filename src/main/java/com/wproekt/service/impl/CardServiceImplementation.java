package com.wproekt.service.impl;

import com.wproekt.model.Card;
import com.wproekt.model.Label;
import com.wproekt.model.Note;
import com.wproekt.model.User;
import com.wproekt.model.exceptions.UserDoesntExistException;
import com.wproekt.repository.CardRepository;
import com.wproekt.repository.LabelRepository;
import com.wproekt.repository.NoteRepository;
import com.wproekt.repository.UserRepository;
import com.wproekt.service.CardService;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;


@Service
public class CardServiceImplementation implements CardService {

    CardRepository cardRepository;
    UserRepository userRepository;
    NoteRepository noteRepository;
    LabelRepository labelRepository;

    public CardServiceImplementation(CardRepository cardRepository, UserRepository userRepository, NoteRepository noteRepository, LabelRepository labelRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.labelRepository = labelRepository;
    }

    @Override
    public boolean putCardInArchive(User user, Long id) {
        if (cardRepository.existsById(id)) {
            Card card = cardRepository.getReferenceById(id);

            if (userRepository.existsByCardsContains(card)) {
                card.setIsArchived(true);
                cardRepository.save(card);

                return true;
            }

        }
        return false;
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

        }
        return false;
    }

    //TODO: site metodi da proveruva dali postoj kartickata
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

        text = text.replaceAll("\n", "<br>");

        String sanitizedText = Jsoup.clean(text, Safelist.none());

        card.setText(sanitizedText);

        cardRepository.save(card);
        return card;
    }

    @Override
    public Label addLabel(String username, Long cardId, Long labelId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserDoesntExistException::new);

        Card card = cardRepository.getReferenceById(cardId);
        Label label = labelRepository.getReferenceById(labelId);

        if (user.getLabels().contains(label)) {
            card.getLabel().add(label);
        }

        cardRepository.save(card);
        return label;
    }

    @Override
    public Card removeLabel(String username, Long cardId, Long labelId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserDoesntExistException::new);

        Card card = cardRepository.getReferenceById(cardId);
        Label label = labelRepository.getReferenceById(labelId);

        if (user.getLabels().contains(label)) {
            card.getLabel().remove(label);
        }

        return cardRepository.save(card);
    }

    @Override
    public Card togglePin(Long cardId) {
        Card card = cardRepository.getReferenceById(cardId);

        Boolean pinned = card.getIsPinned();
        card.setIsPinned(!pinned);

        return cardRepository.save(card);
    }
}
