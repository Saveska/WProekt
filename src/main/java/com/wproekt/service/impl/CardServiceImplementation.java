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
import com.wproekt.service.UserService;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class CardServiceImplementation implements CardService {

    CardRepository cardRepository;
    UserRepository userRepository;
    UserService userService;
    NoteRepository noteRepository;
    LabelRepository labelRepository;

    public CardServiceImplementation(CardRepository cardRepository, UserRepository userRepository, UserService userService, NoteRepository noteRepository, LabelRepository labelRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.userService = userService;
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
    public void editImageCard(User user, Long id, MultipartFile file) {
        Card card = cardRepository.getReferenceById(id);

        String usernameHash = userService.getHashedUsername(user);
        try {
            Files.createDirectories(Paths.get(UserService.getUploadDir(), usernameHash));
            String uuid = UUID.randomUUID().toString();
            Path fileNameAndPath = Paths.get(Paths.get(UserService.getUploadDir(), usernameHash).toString(), uuid);

            String imgPath = String.valueOf(Path.of(UserService.getStaticDir()).relativize(fileNameAndPath));

            card.setImageLink(imgPath);
            cardRepository.save(card);

            Files.write(fileNameAndPath, file.getBytes());


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

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
    public Card togglePin(Long cardId, Integer xPos, Integer yPos) {
        Card card = cardRepository.getReferenceById(cardId);

        Boolean pinned = card.getIsPinned();
        card.setIsPinned(!pinned);
        card.setXPosition(xPos);
        card.setYPosition(yPos);

        return cardRepository.save(card);
    }

    @Override
    public List<Card> reorderUsersCard(String username, List<Object> cardIds) {
        User user = userRepository.findByUsername(username).orElseThrow(UserDoesntExistException::new);

        List<Card> userCards = user.getCards();
//        System.out.println(stringObjectMap);
//        AtomicInteger position = new AtomicInteger();
//        stringObjectMap.forEach((cardId,obj)->{
//
//            Integer xPos = Integer.valueOf(obj.toString().split(",")[0].split("=")[1]);
//            Integer yPos = Integer.valueOf(obj.toString().split(",")[1].split("=")[1].replace("}",""));
//
//            if(userCards.stream().anyMatch(card-> Objects.equals(card.getId(), Long.valueOf(cardId)))){
//                Card cardObj = userCards.stream().filter(card-> Objects.equals(card.getId(), Long.valueOf(cardId))).findFirst().get();
//                cardObj.setXPosition(xPos);
//                cardObj.setYPosition(yPos);
//                cardObj.setPosition(position.get());
//            }
//            position.getAndIncrement();
//
//        });
        //TODO: od ovde
        for (int i = 0; i < cardIds.size(); i++) {
            Long id = Long.parseLong(cardIds.get(i).toString());
            System.out.println(cardIds.get(i));
            System.out.println(userCards);
            System.out.println(id);
            if (userCards.stream().anyMatch(card -> card.getId().equals(id))) {
                System.out.println("test");
                userCards.stream().filter(card -> card.getId().equals(id)).findFirst().get().setPosition(i);
            }
        }
        return cardRepository.saveAll(userCards);

    }

    @Override
    public Card deleteImage(String username, Long cardId) {
        //TODO: da se proveri dali e od user

        Card card = cardRepository.getReferenceById(cardId);
        File image = new File(UserService.getStaticDir() + '\\' + card.getImageLink());


        image.delete();
        card.setImageLink(null);

        return cardRepository.save(card);
    }

    @Override
    public Card restoreCard(Long cardId) {
        Card card = cardRepository.getReferenceById(cardId);

        card.setIsArchived(false);
        card.setIsInBin(false);

        return cardRepository.save(card);
    }

    @Override
    @Transactional
    public boolean deletePermanently( Long cardId) {
        Card card = cardRepository.getReferenceById(cardId);

        card.setLabel(new HashSet<>());
        cardRepository.deleteFromUserCards(cardId);

        cardRepository.delete(card);

        return true;
    }
}
