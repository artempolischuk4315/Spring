package ua.polischuk.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ua.polischuk.entity.Test;
import ua.polischuk.entity.User;
import ua.polischuk.service.TestService;
import ua.polischuk.service.UserService;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Optional;


@Slf4j
@Controller
public class AdminController {
    private TestService testService;
    private UserService userService;

    @Autowired
    public AdminController(TestService testService, UserService userService) {
        this.testService = testService;
        this.userService = userService;
    }

    @GetMapping("/add_test")
    @PreAuthorize("hasRole('ADMIN')")
    public String showTestRedactor(){
        return "tests_redactor";
    }

    @PostMapping("/add_test")
    public String addTest(@Valid Test test, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            model.addAttribute("invalidTestData", true);
            return "tests_redactor";
        }

        try{
            testService.saveNewTest(test);
        }catch (EntityExistsException ex){
            log.info(test.getName() + " test with this name is already exist");
            model.addAttribute("message", " test with this name is already exist");
            return "tests_redactor";
        }
        return "redirect:/";
    }

    @GetMapping("/all_tests")
    @PreAuthorize("hasRole('ADMIN')")
    public String showTestMenu(Model model){
        model.addAttribute("tests", testService.getAllTests());
        return "tests_menu";
    }

    @PostMapping("/all_tests")
    public String deleteTest(String name, Model model){
        try{
            testService.disableTest(testService.findTestByName(name));
            log.info(" Is here");
        }catch (EntityNotFoundException ex){
            log.info(" test with this name is not exist");
            model.addAttribute("message", " Test with this name is not exist");
            return "redirect:/all_tests";
        }

        return "redirect:/all_tests";
    }

    @GetMapping("/all_users")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAllUsers(Model model){
        model.addAttribute("users", userService.getAllUsers());
        return "all_users";
    }


    @GetMapping("/allow_test")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAllowMenu(Model model){
        model.addAttribute("tests", testService.getAllTests());
        model.addAttribute("users", userService.getAllUsers());
        return "allow_test_redactor";
    }

    @PostMapping("/allow_test")
    public String allowTests(String email, String testName, Model model){

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("tests", testService.getAllTests());
        try{
            testService.addTestToAvailableByEmailAndNameOfTest(email, testName);
        }catch (EntityNotFoundException ex){
            model.addAttribute("message", "bad email");

            return "allow_test_redactor";
        }

        model.addAttribute("addingSuccessful", true);
        return "allow_test_redactor";
    }

    @PostMapping("/enable_test")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAllTests(Model model, String nameOfTest){
        try{
            testService.enableTest(testService.findTestByName(nameOfTest));
            log.info(" Is here");
        }catch (EntityNotFoundException ex){
            log.info(" test with this name is not exist");
            model.addAttribute("message", " Test with this name is not exist");
            return "redirect:/all_tests";
        }

        return "redirect:/all_tests";

    }

    @PostMapping("/all_users")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAvailableTestsForEnteredUser(Model model, String email){

        Optional<User> user = userService.findByEmail(email);
        if(user.isPresent()){
            model.addAttribute("availableTests", testService.getAvailableTestsByUser(user.get()));
        }
        else{
            model.addAttribute("message", "wrong email");
            model.addAttribute("users", userService.getAllUsers());
            return "all_users";
        }
        return "available_tests_for_selected_user";

    }

}
