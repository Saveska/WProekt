package com.wproekt.web;

import com.wproekt.model.Card;
import com.wproekt.model.User;
import com.wproekt.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class TaskController {

    UserService userService;

    public TaskController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "/home"})
    public String GetMainPage(Authentication authentication,
                              Model model) {
        User currentUser = (User) authentication.getPrincipal();

        List<Card> userCards = userService.getCards(currentUser.getUsername());

        model.addAttribute("cards", userCards);


        return "landingPage";
    }


}
