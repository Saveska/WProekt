package com.wproekt.web;

import com.wproekt.model.*;
import com.wproekt.model.Label;
import com.wproekt.service.*;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;



@Controller
public class TaskController {


    public static List<ColorWrapper> colors = List.of(new ColorWrapper(185, 86, 185),
            new ColorWrapper(0, 109, 170),
            new ColorWrapper(230, 186, 25),
            new ColorWrapper(121, 186, 86),
            new ColorWrapper(76, 86, 186)
    );
    UserService userService;
    TaskService taskService;
    CardService cardService;
    EmailService emailService;






    public TaskController(UserService userService, TaskService taskService, CardService cardService, EmailService emailService) {
        this.userService = userService;
        this.taskService = taskService;
        this.cardService = cardService;
        this.emailService = emailService;

    }

    @GetMapping({"/", "/home"})

    public String GetMainPage(Authentication authentication,
                              Model model) {
        User currentUser = userService.getUserFromAuth(authentication);
//        User currentUser = userService.getUserFromAuth(authentication);

        List<Card> userCards = userService.getHomePageCards(currentUser.getUsername());


        Set<Label> labels = userService.getUserLabels(currentUser.getUsername());
        model.addAttribute("cards", userCards);
        model.addAttribute("colors", colors);
        model.addAttribute("labels", labels);
        model.addAttribute("username", currentUser.getName());

        model.addAttribute("page", "home");


        return "landingPage";
    }


    @GetMapping({"/trash"})
    public String GetTrashPage(Authentication authentication,
                               Model model) {
        User currentUser = userService.getUserFromAuth(authentication);

        List<Card> userCards = userService.getTrashPageCards(currentUser.getUsername());

        model.addAttribute("cards", userCards);
        model.addAttribute("page", "trash");
        model.addAttribute("username", currentUser.getName());

        Set<Label> labels = userService.getUserLabels(currentUser.getUsername());
        model.addAttribute("labels", labels);

        return "landingPage";
    }

//TODO: ko ce nema labels da se pojavuva poraka

    @PostMapping("/uploadimage")
    public String PostUploadImage(Authentication authentication,
                                  @RequestParam("cardId") Long cardId,
                                  @RequestParam("image") MultipartFile file) throws IOException, NoSuchAlgorithmException {
        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da ne mozi drug user da klaj slika za tret korisnik

        cardService.editImageCard(currentUser, cardId, file);

        return "redirect:/";
    }


    @GetMapping({"/archive"})
    public String GetArchivePage(Authentication authentication,
                                 Model model) {
        User currentUser = userService.getUserFromAuth(authentication);

        List<Card> userCards = userService.getArchivePageCards(currentUser.getUsername());

        model.addAttribute("cards", userCards);
        model.addAttribute("page", "archive");
        model.addAttribute("username", currentUser.getName());

        Set<Label> labels = userService.getUserLabels(currentUser.getUsername());
        model.addAttribute("labels", labels);

        return "landingPage";
    }

}
