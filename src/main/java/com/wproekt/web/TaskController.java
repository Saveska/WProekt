package com.wproekt.web;

import com.wproekt.model.*;
import com.wproekt.service.TaskService;
import com.wproekt.service.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;


@Controller
public class TaskController {

    UserService userService;
    TaskService taskService;

    public TaskController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping({"/", "/home"})
    public String GetMainPage(Authentication authentication,
                              Model model) {
        User currentUser = (User) authentication.getPrincipal();

        List<Card> userCards = userService.getCards(currentUser.getUsername());
        System.out.println(userCards);

        model.addAttribute("cards", userCards);


        return "landingPage";
    }

    @PostMapping("/giveNote")
    @ResponseBody
    public List<Note> notePostAjax(Authentication authentication, @RequestBody String requestData) {
        User currentUser = (User) authentication.getPrincipal();
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            String title = (String) jo.get("title");
            String text = (String) jo.get("text");

            userService.addNoteCard(currentUser.getUsername(), title, text);


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


            TaskCard card = userService.addTaskCard(currentUser.getUsername(),title);

            List<Task> taskList = taskService.createList(tasks);

            taskService.addTasksToTaskCard(card,taskList);



        } catch (Exception e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }

        //userService.addNoteCard(currentUser.getUsername(), )
        return new ArrayList<>();

    }

}
