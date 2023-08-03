package com.wproekt.service.impl;

import com.wproekt.model.Card;
import com.wproekt.model.User;
import com.wproekt.repository.CardRepository;
import com.wproekt.repository.UserRepository;
import com.wproekt.service.CardService;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImplementation implements CardService {

    CardRepository cardRepository;
    UserRepository userRepository;

    public CardServiceImplementation(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean putCardInBin(User user, Long id) {
        if(cardRepository.existsById(id)){
            Card card = cardRepository.getReferenceById(id);
            if(userRepository.existsByCardsContains(card)){
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
}
