package com.wproekt.web;

import com.wproekt.model.exceptions.InvalidRepeatPassword;
import com.wproekt.service.EmailService;
import com.wproekt.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;


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

    @PostMapping("/resetPassword")
    public String postResetPassword(@RequestParam String username,
                                    HttpServletRequest request,
                                    Model model) {

        try {
            String host = request.getServerName() + ':' + request.getServerPort();
            userService.resetPassword(username, host);
        } catch (Exception exception) {
            model.addAttribute("content",exception.getMessage());
            return "infoPage";
        }
        model.addAttribute("content", "A password reset link has been sent to your associated E-Mail address!");
        return "infoPage";


    }

    @GetMapping("/reset/{username}/{token}")
    public String VerifyResetToken(@PathVariable String username, @PathVariable String token, Model model) {

        model.addAttribute("username", username);
        model.addAttribute("token", token);



        return "resetPassPage";
    }
    @PostMapping("/resetForm")
    public String PasswordResetForm(@RequestParam String username,
                                    @RequestParam String token,
                                    @RequestParam String password,
                                    @RequestParam String repeatPassword,
                                    Model model){

        try{
            if(!Objects.equals(password, repeatPassword)){
                throw new InvalidRepeatPassword();
            }
            userService.verifyResetToken(username, token);
            userService.changePassword(username,password);
        }catch (Exception exception){
            model.addAttribute("content",exception.getMessage());
            return "infoPage";
        }
        model.addAttribute("content","Password successfully changed! You can login now.");

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
