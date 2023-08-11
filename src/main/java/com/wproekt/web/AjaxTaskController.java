package com.wproekt.web;

import com.wproekt.model.*;
import com.wproekt.service.CardService;
import com.wproekt.service.TaskService;
import com.wproekt.service.UserService;
import com.wproekt.service.UtilService;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
public class AjaxTaskController {

    UserService userService;
    TaskService taskService;
    CardService cardService;
    UtilService utilService;

    public AjaxTaskController(UserService userService, TaskService taskService, CardService cardService, UtilService utilService) {
        this.userService = userService;
        this.taskService = taskService;
        this.cardService = cardService;
        this.utilService = utilService;
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
            e.printStackTrace();
        }

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


            JSONObject jsonArray = jo.getJSONObject("allTasks");
            tasks = jsonArray.toMap();

            TaskCard card = userService.addTaskCard(currentUser.getUsername(), title);
            List<Task> taskList = taskService.createList(tasks);

            taskService.addTasksToTaskCard(card, taskList);

        } catch (Exception e) {

            e.printStackTrace();
        }

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

            Long id = jo.getLong("id");
            Boolean checked = jo.getBoolean("checked");

            taskService.setTaskBoolean(id, checked);

        } catch (Exception e) {

            e.printStackTrace();
        }


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

            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @PostMapping("/changeColor")
    @ResponseBody
    public Boolean changeColorCard(Authentication authentication,
                                   @RequestBody String requestData) {
        User currentUser = (User) authentication.getPrincipal();
        //TODO: da se proveri dali kartickata e od korisniko
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            Long cardId = jo.getLong("id");

            String colorString = jo.getString("base").replace(")", "");
            colorString = colorString.substring(4);

            String[] split = colorString.split(",");

            int red = Integer.parseInt(split[0]);
            int green = Integer.parseInt(split[1]);
            int blue = Integer.parseInt(split[2]);

            cardService.setColor(cardId, red, green, blue);
            return true;


        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("/editCard")
    @ResponseBody
    public Boolean editCardAjax(Authentication authentication,
                                @RequestBody String requestData) {
        User currentUser = (User) authentication.getPrincipal();
        //TODO: da se proveri dali e od korisniko kartickata
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);


            JSONObject jo = new JSONObject(decodedData);

            Long id = jo.getLong("id");
            String text = jo.getString("text");
            String type = jo.getString("type");

            switch (type) {
                case "title" -> cardService.editTitleCard(id, text);
                case "text" -> cardService.editTextCard(id, text);
                case "task" -> taskService.editTask(id, text);
            }

            return true;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("/addLabel")
    @ResponseBody
    public List<String> addLabelAjax(Authentication authentication,
                                     @RequestBody String requestData) {
        User currentUser = (User) authentication.getPrincipal();

        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            String labelName = jo.getString("name");

            Label label = userService.addLabelToUser(currentUser.getUsername(), labelName);

            return List.of(String.valueOf(label.getId()), utilService.cleanHtml(label.getName()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    @PostMapping("/removeLabel")
    @ResponseBody
    @Transactional
    public Boolean removeLabelAjax(Authentication authentication,
                                     @RequestBody String requestData) {
        User currentUser = (User) authentication.getPrincipal();

        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            Long labelId = jo.getLong("id");
            userService.removeLabelFromUser(currentUser, labelId);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    @PostMapping("/checkLabel")
    @ResponseBody
    public Boolean checkLabelAjax(Authentication authentication,
                                  @RequestBody String requestData) {
        User currentUser = (User) authentication.getPrincipal();
        //TODO: da se proveri dali label pripajdza na user
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            Long labelId = jo.getLong("labelId");
            Long cardId = jo.getLong("cardId");

            boolean checked = jo.getBoolean("checked");


            if (checked) {
                cardService.addLabel(currentUser, cardId, labelId);
            } else {
                cardService.removeLabel(currentUser, cardId, labelId);
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
