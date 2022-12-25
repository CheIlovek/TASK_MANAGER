package ru.tskmngr.task_manager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class LoginController {
    @GetMapping("/registration/login")
    String login() {
        return "/registration/login";
    }
}