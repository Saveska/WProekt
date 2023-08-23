package com.wproekt.service.impl;

import com.wproekt.model.*;
import com.wproekt.model.exceptions.*;
import com.wproekt.repository.CardRepository;
import com.wproekt.repository.LabelRepository;
import com.wproekt.repository.UserRepository;
import com.wproekt.service.EmailService;
import com.wproekt.service.UserService;
import org.hibernate.Session;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final EmailService emailService;
    private final LabelRepository labelRepository;
    private final PasswordEncoder passwordEncoder;
    @PersistenceContext
    private final EntityManager entityManager;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();


    public UserServiceImplementation(UserRepository userRepository, CardRepository cardRepository, EmailService emailService, LabelRepository labelRepository, PasswordEncoder passwordEncoder, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.emailService = emailService;
        this.labelRepository = labelRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    private String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    @Override
    public User reattachUser(User user) {
        Session session = entityManager.unwrap(Session.class);
        User reattachedUser = (User) session.merge(user);
        session.flush(); // Optional, can be useful to synchronize changes
        return reattachedUser;
    }

    @Override
    public User register(String username, String password, String repeatPassword, String name, String surname, String email, String host) {
        if (username == null || password == null || repeatPassword == null || email == null) {
            throw new EmptyUserInformationException();
        }

        if (userRepository.existsUserByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExists();
        }

        if (!realMail(email)) {
            throw new EmailDoesntExist();
        }

        if (userRepository.existsUserByUsername(username)) {
            throw new UsernameAlreadyExists();
        }

        if (!password.equals(repeatPassword)) {
            throw new InvalidRepeatPassword();
        }

        User user = new User(username, email, passwordEncoder.encode(password), name, surname);

        String token = generateNewToken();
        user.setToken(token);

        emailService.sendTokenMail(email, token, host, username);

        return userRepository.save(user);
    }

    private boolean realMail(String email) {
        return email.matches("[A-Z0-9._%+-][A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{3}");
    }

    @Override
    public List<Card> getHomePageCards(String username) {

        if (userRepository.findByUsername(username).isPresent()) {
            User user = userRepository.findByUsername(username).get();
            List<Card> cards = user.getCards();
            cards = cards.stream().filter(card -> !card.getIsArchived() && !card.getIsInBin()).sorted(Comparator.comparing(Card::getPosition).thenComparing(Card::getDateLastUpdated)).toList();

            return cards;

        } else {
            throw new UserDoesntExistException();
        }
    }


    @Override
    public List<Card> getTrashPageCards(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            User user = userRepository.findByUsername(username).get();
            List<Card> cards = user.getCards();
            cards = cards.stream().filter(Card::getIsInBin).toList();
            return cards;

        } else {
            throw new UserDoesntExistException();
        }
    }


    @Override
    public List<Card> getArchivePageCards(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            User user = userRepository.findByUsername(username).get();
            List<Card> cards = user.getCards();
            cards = cards.stream().filter(Card::getIsArchived).toList();
            return cards;

        } else {
            throw new UserDoesntExistException();
        }
    }

    @Override
    public List<Card> getLabelFilteredCards(String username, Long labelId) {
        if (userRepository.findByUsername(username).isPresent()) {
            User user = userRepository.findByUsername(username).get();
            Label label = labelRepository.getReferenceById(labelId);

            List<Card> cards = user.getCards();
            cards = cards.stream().filter(card -> card.getLabel().contains(label)).toList();
            return cards;

        } else {
            throw new UserDoesntExistException();
        }
    }

    @Override
    public Note addNoteCard(String username, String title, String text) {
        if (userRepository.findByUsername(username).isPresent()) {
            User user = userRepository.findByUsername(username).get();
            Note note = new Note(title, text);

            cardRepository.save(note);
            user.getCards().add(note);
            userRepository.save(user);

            return note;

        } else {
            throw new UserDoesntExistException();
        }

    }

    @Override
    public TaskCard addTaskCard(String username, String title) {
        if (userRepository.findByUsername(username).isPresent()) {
            List<Card> cards = userRepository.findByUsername(username).get().getCards();

            User user = userRepository.findByUsername(username).get();
            TaskCard note = new TaskCard(title);

            cardRepository.save(note);
            user.getCards().add(note);
            userRepository.save(user);

            return note;

        } else {
            throw new UserDoesntExistException();
        }
    }

    @Override
    public boolean verifyToken(String username, String token) {
        User user = userRepository.findByUsername(username).orElseThrow(UserDoesntExistException::new);
        if (user.getToken().equals(token)) {
            user.setToken(null);
        } else {
            throw new WrongTokenException();
        }

        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional
    public Set<Label> getUserLabels(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserDoesntExistException::new);

        return user.getLabels();

    }

    @Override
    public Label addLabelToUser(String username, String label) {

        User user = userRepository.findByUsername(username).orElseThrow(UserDoesntExistException::new);
        Label newLabel = new Label(label);

        user.getLabels().add(newLabel);

        System.out.println(user.getLabels());
        labelRepository.save(newLabel);

        userRepository.save(user);
        return newLabel;
    }

    @Override
    public void removeLabelFromUser(String username, Long labelId) {
//        Label label = labelRepository.getReferenceById(labelId);
//        User reattached = reattachUser(user);

//        List<Card> cards = reattached.getCards();

//        cards.forEach(card->{
//            card.getLabel().remove(label);
//
//        });
//        cardRepository.saveAll(cards);
//        reattached.getLabels().remove(label);
        userRepository.deleteLabelFromUser(labelId);
        labelRepository.deleteFromCardLabel(labelId);

        labelRepository.deleteById(labelId);


    }

    @Override
    public String getHashedUsername(User user) {

        if (user.getHashedUsername() == null) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                messageDigest.update(user.getUsername().getBytes());
                String usernameHash = DatatypeConverter.printHexBinary(messageDigest.digest());
                user.setHashedUsername(usernameHash);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        userRepository.save(user);

        return user.getHashedUsername();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmailIgnoreCase(username, username).orElseThrow(UserDoesntExistException::new);
    }
}
