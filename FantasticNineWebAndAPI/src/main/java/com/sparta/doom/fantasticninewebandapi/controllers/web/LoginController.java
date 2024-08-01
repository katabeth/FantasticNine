package com.sparta.doom.fantasticninewebandapi.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "fragments/login/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "home";
    }
}
