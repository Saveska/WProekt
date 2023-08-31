package com.wproekt.service.impl;

import com.wproekt.model.User;
import com.wproekt.repository.UserRepository;
import com.wproekt.service.OAuthUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuthUserServiceImplementation extends DefaultOAuth2UserService implements OAuthUserService {
    UserRepository userRepository;

    public OAuthUserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);


        // Extract relevant attributes
        String username = oauth2User.getAttribute("username");


        // Check if the user with the given email exists in your database

        if (userRepository.findByUsername(username).isPresent()) {


            // Create a new user or handle as needed
            User user = new User();
            user.setUsername(username);
            // ... set other attributes

            userRepository.save(user);
        }

        return oauth2User;
    }
}

