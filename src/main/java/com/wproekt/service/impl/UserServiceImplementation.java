package com.wproekt.service.impl;

import com.wproekt.model.User;
import com.wproekt.model.exceptions.*;
import com.wproekt.repository.UserRepository;
import com.wproekt.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username, username).orElseThrow(UserDoesntExistException::new);
    }
}
