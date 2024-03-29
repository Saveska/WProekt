package com.wproekt.web;

import com.wproekt.model.*;
import com.wproekt.service.CardService;
import com.wproekt.service.TaskService;
import com.wproekt.service.UserService;
import com.wproekt.service.UtilService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
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
        User currentUser = userService.getUserFromAuth(authentication);

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
    public List<TaskCard> taskPostAjax(Authentication authentication, @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);
        Map<String, Object> tasks;

        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            String title = jo.getString("title");


            JSONObject jsonArray = jo.getJSONObject("allTasks");
            tasks = jsonArray.toMap();

            TaskCard card = userService.addTaskCard(currentUser.getUsername(), title);
            List<Task> taskList = taskService.createList(tasks);


            TaskCard taskCard = taskService.addTasksToTaskCard(card, taskList);

            return List.of(taskCard);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @PostMapping("/changeStatus")
    @ResponseBody
    public List<Note> taskChangeAjax(Authentication authentication, @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);
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

    @PostMapping("/archiveCard")
    @ResponseBody
    public List<Note> archiveCardAjax(Authentication authentication, @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);

        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);
            Long id = jo.getLong("id");

            cardService.putCardInArchive(currentUser, id);

        } catch (Exception e) {

            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @PostMapping("/binCard")
    @ResponseBody
    public List<Note> binCardAjax(Authentication authentication, @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);

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
        User currentUser = userService.getUserFromAuth(authentication);
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
        User currentUser = userService.getUserFromAuth(authentication);
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

    @PostMapping("/addTaskToCard")
    @ResponseBody
    public List<String> addTaskToCard(Authentication authentication,
                                      @RequestBody String requestData) {

        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da se proveri dali e od korisniko kartickata
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);


            JSONObject jo = new JSONObject(decodedData);

            Long cardId = jo.getLong("id");
            String text = jo.getString("text");



            String cleanText = utilService.cleanHtml(text);
            Task task = taskService.addTask(cardId, cleanText);
            return List.of(cleanText, String.valueOf(cardId), String.valueOf(task.getId()));

        } catch (Exception e) {

            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @PostMapping("/removeTaskFromCard")
    @ResponseBody
    public Boolean RemoveTaskFromCard(Authentication authentication,
                                      @RequestBody String requestData) {

        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da se proveri dali e od korisniko kartickata
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);


            JSONObject jo = new JSONObject(decodedData);

            Long taskId = jo.getLong("taskId");
            Long cardId = jo.getLong("cardId");

            taskService.deleteTask(taskId, cardId);

            return true;

        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("/changeCardOfTask")
    @ResponseBody
    public Boolean changeOrder(Authentication authentication,
                               @RequestBody String requestData) {

        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da se proveri dali e od korisniko kartickata
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);


            JSONObject jo = new JSONObject(decodedData);

            Long siblingId = null;

            Long taskId = jo.getLong("taskId");
            boolean siblingExists = jo.getBoolean("siblingExists");

            if (siblingExists) {
                siblingId = jo.getLong("siblingId");
            }

            Long cardSource = jo.getLong("cardSource");
            Long cardTarget = jo.getLong("cardTarget");



            taskService.changeCardOfTask(taskId, siblingId, cardSource, cardTarget);

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
        User currentUser = userService.getUserFromAuth(authentication);

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
        User currentUser = userService.getUserFromAuth(authentication);

        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            Long labelId = jo.getLong("id");
            userService.removeLabelFromUser(currentUser.getUsername(), labelId);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    @PostMapping("/checkLabel")
    @ResponseBody
    public List<String> checkLabelAjax(Authentication authentication,
                                       @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da se proveri dali label pripajdza na user
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            Long labelId = jo.getLong("labelId");
            Long cardId = jo.getLong("cardId");

            boolean checked = jo.getBoolean("checked");


            if (checked) {
                Label label = cardService.addLabel(currentUser.getUsername(), cardId, labelId);
                String cleanText = Jsoup.clean(label.getName(), Safelist.simpleText());

                return List.of(cleanText, String.valueOf(labelId));

            } else {
                cardService.removeLabel(currentUser.getUsername(), cardId, labelId);
            }
            return new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @PostMapping("/togglePin")
    @ResponseBody
    public List<String> togglePinAjax(Authentication authentication,
                                      @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da se proveri dali card pripajdza na user
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            Long cardId = jo.getLong("cardId");
            Integer xPos = jo.getInt("xPos");
            Integer yPos = jo.getInt("yPos");

            cardService.togglePin(cardId, xPos, yPos);

            return new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @PostMapping("/orderCards")
    @ResponseBody
    public List<String> orderCards(Authentication authentication,
                                   @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da se proveri dali card pripajdza na user
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);


            List<Object> array = jo.getJSONArray("order").toList();
            cardService.reorderUsersCard(currentUser.getUsername(), array);

            return new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @PostMapping("/deleteImage")
    @ResponseBody
    public boolean deleteImage(Authentication authentication,
                               @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da se proveri dali card pripajdza na user
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            Long cardId = jo.getLong("cardId");

            cardService.deleteImage(currentUser.getUsername(), cardId);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @PostMapping("/restoreCard")
    @ResponseBody
    public boolean restoreCardAjax(Authentication authentication,
                                   @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da se proveri dali card pripajdza na user
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            Long cardId = jo.getLong("cardId");

            Card card = cardService.restoreCard(cardId);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @PostMapping("/deleteCardPermanent")
    @ResponseBody
    public boolean deleteCardAjax(Authentication authentication,
                                  @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da se proveri dali card pripajdza na user
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            Long cardId = jo.getLong("cardId");


            return cardService.deletePermanently(cardId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @PostMapping("/restoreAll")
    @ResponseBody

    public boolean restoreAllCardsAjax(Authentication authentication,
                                       @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da se proveri dali card pripajdza na user
        try {

            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            String type = jo.getString("type");

            userService.restoreAllCards(currentUser.getUsername(), type);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @PostMapping("/deleteAll")
    @ResponseBody

    public boolean deleteAllCardsAjax(Authentication authentication,
                                      @RequestBody String requestData) {
        User currentUser = userService.getUserFromAuth(authentication);
        //TODO: da se proveri dali card pripajdza na user
        try {
            String decodedData = URLDecoder.decode(requestData, UTF_8);

            JSONObject jo = new JSONObject(decodedData);

            String type = jo.getString("type");


            userService.deleteAllCards(currentUser.getUsername(), type);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
