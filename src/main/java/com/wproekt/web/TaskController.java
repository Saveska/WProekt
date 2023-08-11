package com.wproekt.web;

import com.wproekt.model.*;
import com.wproekt.model.Label;
import com.wproekt.service.CardService;
import com.wproekt.service.EmailService;
import com.wproekt.service.TaskService;
import com.wproekt.service.UserService;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;


@Controller
public class TaskController {
    public static String STATIC_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static";
    public static String UPLOAD_DIRECTORY = STATIC_DIRECTORY + "/uploads";

    public static List<Color> colors = List.of(new Color(185, 86, 185),
            new Color(0, 109, 170),
            new Color(230, 186, 25),
            new Color(121, 186, 86),
            new Color(76, 86, 186)
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

        User currentUser = (User) authentication.getPrincipal();

        List<Card> userCards = userService.getHomePageCards(currentUser.getUsername());


        Set<Label> labels = userService.getUserLabels(currentUser.getUsername());
        model.addAttribute("cards", userCards);
        model.addAttribute("colors", colors);
        model.addAttribute("labels", labels);

        model.addAttribute("page", "home");


        return "landingPage";
    }


    @GetMapping({"/trash"})
    public String GetTrashPage(Authentication authentication,
                               Model model) {
        User currentUser = (User) authentication.getPrincipal();

        List<Card> userCards = userService.getTrashPageCards(currentUser.getUsername());

        model.addAttribute("cards", userCards);
        model.addAttribute("page", "trash");

        return "landingPage";
    }

    @PostMapping("/uploadimage")
    public String PostUploadImage(Authentication authentication,
                                  @RequestParam("cardId") Long cardId,
                                  @RequestParam("image") MultipartFile file) throws IOException, NoSuchAlgorithmException {
        User currentUser = (User) authentication.getPrincipal();
        //TODO: da ne mozi drug user da klaj slika za tret korisnik

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(currentUser.getUsername().getBytes());
        String usernameHash = DatatypeConverter.printHexBinary(messageDigest.digest());

        //TODO: ova vo service klaj go
        System.out.println(Files.createDirectories(Paths.get(UPLOAD_DIRECTORY, usernameHash)));
        Path fileNameAndPath = Paths.get(Paths.get(UPLOAD_DIRECTORY, usernameHash).toString(), file.getOriginalFilename());

        String imgPath = String.valueOf(Path.of(STATIC_DIRECTORY).relativize(fileNameAndPath));
        cardService.editImageCard(cardId, imgPath);


        Files.write(fileNameAndPath, file.getBytes());

        return "redirect:/";
    }


}
