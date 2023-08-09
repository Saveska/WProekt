package com.wproekt.web;

import com.wproekt.service.EmailService;
import com.wproekt.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@Controller
public class LoginController {
    UserService userService;
    EmailService emailService;

    public LoginController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/login")
    public String GetLoginPage(@RequestParam(required = false) String err, Model model

    ) {
        //TODO: zacisti go malce kodov plz
        //TODO: exceptions za logino
        if (err != null) {
            switch (err) {
                case "invalid-credentials" -> model.addAttribute("err", "Invalid username or password");
                case "not-verified" ->
                        model.addAttribute("err", "Your account hasn't been verified. Check your email!");
                case "unknown" -> model.addAttribute("err", "Please try again");
                case "Email Already Exists", "Please check your E-Mail address", "Please fill out all of the fields!",
                        "Passwords need to match", "Username Already Exists" -> model.addAttribute("err", err);

            }
        }
        return "loginPage";
    }

    @PostMapping("/register")
    public String PostRegister(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String surname,
                               @RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String repeatPassword,
                               @RequestParam String email,
                               HttpServletRequest request,
                               Model model
    ) {
        try {
            String host = request.getServerName() + ':' + request.getServerPort();
            userService.register(username, password, repeatPassword, name, surname, email, host);

        } catch (Exception exception) {
            return String.format("redirect:/login?err=%s", exception.getMessage());
        }

        model.addAttribute("content", "Successful registration, please check your mail to verify your account");
        return "infoPage";
    }

    @GetMapping("/verify/{username}/{token}")
    public String VerifyUser(@PathVariable String username, @PathVariable String token, Model model) {
        try {
            userService.verifyToken(username, token);
            model.addAttribute("content", "Account succesfully activated, you can now login");
            model.addAttribute("error", false);

        } catch (Exception e) {
            model.addAttribute("content", e.getMessage());
            model.addAttribute("error", true);

        }

        return "infoPage";
    }
}
