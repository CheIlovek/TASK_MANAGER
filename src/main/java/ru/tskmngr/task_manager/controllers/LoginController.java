package ru.tskmngr.task_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.tskmngr.task_manager.models.User;
import ru.tskmngr.task_manager.service.UserService;

import javax.validation.Valid;

@Controller
class LoginController {

    @Autowired
    private UserService userService;
    @GetMapping("/registration/login")
    String login() {
        return "/registration/login";
    }

    @GetMapping("/registration/signup")
    public String signUp(){
        return "/registration/signup";
    }

    @PostMapping("/registration/signup")
    public String newUser(@ModelAttribute("userForm")@Valid User userForm,
    BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()) {
            model.addAttribute("error");
            return "/registration/signup";
        }
//        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
//            model.addAttribute("passwordError", "Пароли не совпадают");
//            return "registration";
//        }
        if (!userService.saveUser(userForm)){
            model.addAttribute("taken");
            return "/registration/signup";
        }

        return "redirect:/";
    }

}