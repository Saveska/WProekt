package com.wproekt.web;

import com.wproekt.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {
    UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String GetLoginPage(@RequestParam(required = false) String err,
                               Model model) {

        model.addAttribute("err", err);
        return "loginPage";
    }

    @PostMapping("/register")
    public String PostRegister(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String surname,
                               @RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email
    ) {
        try {
            userService.register(username, password, password, name, surname, email);
        } catch (Exception exception) {

            return String.format("redirect:/login?err=%s", exception.getMessage());
        }

        return "redirect:/login";
    }
}
