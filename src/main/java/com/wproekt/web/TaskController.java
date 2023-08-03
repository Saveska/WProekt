package com.wproekt.web;

import com.wproekt.model.*;
import com.wproekt.service.CardService;
import com.wproekt.service.TaskService;
import com.wproekt.service.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;


@Controller
public class TaskController {
    public static String STATIC_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static";
    public static String UPLOAD_DIRECTORY = STATIC_DIRECTORY + "/uploads";
    UserService userService;
    TaskService taskService;
    CardService cardService;

    public TaskController(UserService userService, TaskService taskService, CardService cardService) {
        this.userService = userService;
        this.taskService = taskService;
        this.cardService = cardService;
    }

    @GetMapping({"/", "/home"})
    public String GetMainPage(Authentication authentication,
                              Model model) {
        User currentUser = (User) authentication.getPrincipal();

        List<Card> userCards = userService.getHomePageCards(currentUser.getUsername());

        model.addAttribute("cards", userCards);
        model.addAttribute("page", "home");

        return "landingPage";
    }


    @GetMapping({"/trash"})
    public String GetTrashPage(Authentication authentication,
                               Model model) {
        User currentUser = (User) authentication.getPrincipal();

        List<Card> userCards = userService.getTrashPageCards(currentUser.getUsername());
        System.out.println(userCards);

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

        System.out.println(Files.createDirectories(Paths.get(UPLOAD_DIRECTORY,usernameHash)));
        Path fileNameAndPath = Paths.get(Paths.get(UPLOAD_DIRECTORY,usernameHash).toString(),file.getOriginalFilename());

        String imgPath = String.valueOf(Path.of(STATIC_DIRECTORY).relativize(fileNameAndPath));
        cardService.editImageCard(cardId,imgPath);


        Files.write(fileNameAndPath, file.getBytes());

        return "redirect:/";
    }

    //AJAX CALLS
    @PostMapping("/giveNote")
    @ResponseBody
    public List<Note> notePostAjax(Authentication authentication, @RequestBody String requestData) {
        User currentUser = (User) authentication.getPrincipal();
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            String title = (String) jo.get("title");
            String text = (String) jo.get("text");

            Note note = userService.addNoteCard(currentUser.getUsername(), title, text);
            List<Note> notes = new ArrayList<>();

            notes.add(note);
            return notes;

        } catch (Exception e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }

        //userService.addNoteCard(currentUser.getUsername(), )
        return new ArrayList<>();

    }

    @PostMapping("/giveTask")
    @ResponseBody
    public List<Note> taskPostAjax(Authentication authentication, @RequestBody String requestData) {
        User currentUser = (User) authentication.getPrincipal();
        Map<String, Object> tasks;
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);


            JSONObject jo = new JSONObject(decodedData);
            System.out.println(jo);
            String title = jo.getString("title");


//            System.out.println(jo.get("allTasks"));
            JSONObject jsonArray = jo.getJSONObject("allTasks");
            tasks = jsonArray.toMap();
            System.out.println(tasks);


            TaskCard card = userService.addTaskCard(currentUser.getUsername(), title);

            List<Task> taskList = taskService.createList(tasks);

            taskService.addTasksToTaskCard(card, taskList);


        } catch (Exception e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }

        //userService.addNoteCard(currentUser.getUsername(), )
        return new ArrayList<>();

    }

    @PostMapping("/changeStatus")
    @ResponseBody
    public List<Note> taskChangeAjax(Authentication authentication, @RequestBody String requestData) {
        User currentUser = (User) authentication.getPrincipal();
        //TODO: mislam deka mozi anon user da smeni tujdz task, FIX
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);


            JSONObject jo = new JSONObject(decodedData);
            System.out.println(jo);
            Long id = jo.getLong("id");
            Boolean checked = jo.getBoolean("checked");

            System.out.println(id);
            System.out.println(checked);

            taskService.setTaskBoolean(id, checked);

        } catch (Exception e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }

        //userService.addNoteCard(currentUser.getUsername(), )
        return new ArrayList<>();

    }

    @PostMapping("/binCard")
    @ResponseBody
    public List<Note> binCardAjax(Authentication authentication, @RequestBody String requestData) {
        User currentUser = (User) authentication.getPrincipal();

        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);


            JSONObject jo = new JSONObject(decodedData);
            Long id = jo.getLong("id");

            cardService.putCardInBin(currentUser, id);


        } catch (Exception e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }

        //userService.addNoteCard(currentUser.getUsername(), )
        return new ArrayList<>();

    }
}
