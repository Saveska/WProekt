package com.wproekt.config;

import com.wproekt.model.User;
import com.wproekt.model.enums.Role;
import com.wproekt.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    UserRepository userRepository;

    public OAuthSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oAuthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String username = oAuthUser.getAttribute("login");
        String originalUsername = username;

        Random rand = new Random();
        rand.setSeed(345346234);
        while (userRepository.findByUsername(username).isPresent() && !userRepository.findByUsername(username).get().getIsGithub()) {
            Integer num = rand.nextInt(10);
            username += String.valueOf(num);
        }
        if (userRepository.findByUsername(username).isPresent()) {
            new DefaultRedirectStrategy().sendRedirect(request, response, "/home");
        }else{
            User user = new User();
            user.setUsername(username);
            user.setName(originalUsername);
            user.setRole(Role.ROLE_USER);
            user.setIsGithub(true);
            userRepository.save(user);

            new DefaultRedirectStrategy().sendRedirect(request, response, "/home");
        }


    }
}
