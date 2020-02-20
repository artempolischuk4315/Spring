package ua.polischuk.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.polischuk.service.UserService;

import java.util.ResourceBundle;

import static java.util.stream.Collectors.joining;

@Controller
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class MainController {

    private UserService userService;
    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String getHelloPage(Model model) {
        ResourceBundle bundle = ResourceBundle.getBundle("adminAndMail");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        model.addAttribute("email", user.getUsername());
        model.addAttribute("success", userService.findByEmail(user.getUsername()).get().getSuccess());
        model.addAttribute("role", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(joining(",")));

        if(userService.findByEmail(user.getUsername()).get().getRoles().toString().equals("ROLE_ADMIN")){
            return "hello_admin";
        }else {

            return "hello";
        }
    }




}
