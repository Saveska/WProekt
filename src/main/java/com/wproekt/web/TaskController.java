package com.wproekt.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TaskController {

    public TaskController() {

    }

    @GetMapping("/")
    public String GetMainPage(){
        return "landingPage";
    }

    @GetMapping("/login")
    public String GetLoginPage(){
        return "loginPage";
    }

}
