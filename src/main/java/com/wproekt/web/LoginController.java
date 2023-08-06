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
    public String GetLoginPage(@RequestParam(required = false) String err, Model model, HttpServletRequest request

    ) {
        //TODO: zacisti go malce kodov plz
        //TODO: exceptions za logino
        model.addAttribute("err", err);

        return "loginPage";
    }

    @PostMapping("/register")
    public String PostRegister(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String surname,
                               @RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email,
                               HttpServletRequest request,
                               Model model
    ) {
        try {

            String host = request.getServerName() + ':' + request.getServerPort();
            userService.register(username, password, password, name, surname, email, host);
        } catch (Exception exception) {

            return String.format("redirect:/login?err=%s", exception.getMessage());
        }

        model.addAttribute("content", "Successfull registration, please check your mail to verify your account");
        return "infoPage";
    }

    @GetMapping("/verify/{username}/{token}")
    public String VerifyUser(@PathVariable String username, @PathVariable String token, Model model) {
        System.out.println(username);
        System.out.println(token);
        userService.verifyToken(username, token);

        model.addAttribute("content", "Account succesfully activated, you can now login");
        return "infoPage";
    }
}
