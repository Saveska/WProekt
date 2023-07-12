package com.wproekt.service.impl;

import com.wproekt.model.*;
import com.wproekt.model.exceptions.*;
import com.wproekt.repository.CardRepository;
import com.wproekt.repository.UserRepository;
import com.wproekt.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImplementation(UserRepository userRepository, CardRepository cardRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(String username, String password, String repeatPassword, String name, String surname, String email) {
        if (username == null || password == null || repeatPassword == null || email == null) {
            throw new EmptyUserInformationException();
        }
        if (userRepository.existsUserByEmail(email)) {

            throw new EmailAlreadyExists();
        }
        if (userRepository.existsUserByUsername(username)) {
            throw new UsernameAlreadyExists();
        }
        if (!password.equals(repeatPassword)) {
            throw new InvalidRepeatPassword();
        }
        User user = new User(username, email, passwordEncoder.encode(password), name, surname);
        return userRepository.save(user);
    }

    @Override
    public List<Card> getCards(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            User user = userRepository.findByUsername(username).get();

            return user.getCards();
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username, username).orElseThrow(UserDoesntExistException::new);
    }
}
