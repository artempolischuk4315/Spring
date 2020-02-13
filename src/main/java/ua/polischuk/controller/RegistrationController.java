package ua.polischuk.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.polischuk.entity.User;
import ua.polischuk.service.UserService;

import javax.persistence.EntityExistsException;
import javax.validation.Valid;

@Slf4j
@Controller
public class RegistrationController {

    private UserService userService;
    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(@RequestParam(value = "error", required = false) String error,
                               Model model) {

        model.addAttribute("error", error != null);
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@Valid User user, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("registerError", true);
            return "registration";
        }

            try{
            userService.saveNewUser(user);
            model.addAttribute("message", "check your mail!");
            return "registration";
        }catch (EntityExistsException e) {
                model.addAttribute("message1", "user is already exist!");
                return "registration";
            }
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);
        if (isActivated) {
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("message_wrong", "Activation code is not found!");
        }

        return "login";
    }




}
