package ua.polischuk.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.polischuk.entity.Test;
import ua.polischuk.entity.User;
import ua.polischuk.entity.enumsAndRegex.Category;
import ua.polischuk.service.TestService;
import ua.polischuk.service.UserService;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class UserController {
    private TestService testService;
    private UserService userService;

    @Autowired
    public UserController(TestService testService, UserService userService) {
        this.testService = testService;
        this.userService = userService;
    }

    @GetMapping( "/available_tests")
    public String showAvailableTests(String category, Model model){
        UserDetails user = (UserDetails) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        try {

            model.addAttribute("category", Category.valueOf(category));
            Set<Test> tests = userService.getAvailableTests(userService.findByEmail(user.getUsername()).get())
                    .stream()
                    .filter(test -> test.getCategory().toString().equals(category)) //TODO
                    .filter(test -> test.isActive())
                    .collect(Collectors.toSet());

            model.addAttribute("availableTests", tests);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "available_tests";
    }

    @GetMapping( "/completed_tests")
    public String showCompletedTests( Model model){
        UserDetails userDetails = (UserDetails) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        User user =userService.findByEmail(userDetails.getUsername()).get();
        userService.recountSuccessOnMainUserPage(userDetails); //TODO в отельный метод в сервис
        model.addAttribute("success", user.getSuccess());
        model.addAttribute("tests", userService.setResultsOfTestsForPrinting(userDetails));

        return "completed_tests";

    }

    @PostMapping( "/available_tests")
    public String showTestWindow(@RequestParam(value = "error", required = false) String error,
                                 Test test, Model model,
                                 final RedirectAttributes redirectAttributes)  {
        model.addAttribute("test", test);
        UserDetails userDetails = (UserDetails) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        try {
            userService.manageResultAndCompleteTest(userDetails, test);
        }catch (Exception e){
            log.info("Error");
            e.printStackTrace();
            model.addAttribute("error", error != null);

            return "redirect:/";
        }
        redirectAttributes.addFlashAttribute("test", test);
        return "redirect:/test_over";
    }

    @GetMapping( "/test_over")
    public String showTestsOver(@ModelAttribute("test") Test test, Model model){

        try {
            test = testService.findTestByName(test.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }

        log.info("Test is "+test);
        model.addAttribute("test", test);

        return "test_over";

    }
    @PostMapping("/test_over")
    public String sendResult(Test test)  {
        UserDetails userDetails = (UserDetails) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        try {
            userService.sendResult(userDetails, test);
        }catch (Exception e){
            return "test_over";
        }
        return "redirect:/";
    }
}
