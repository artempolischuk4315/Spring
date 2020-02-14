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
import ua.polischuk.service.CompletingTestService;
import ua.polischuk.service.MailSender;
import ua.polischuk.service.TestService;
import ua.polischuk.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class UserController {
    private TestService testService;
    private UserService userService;
    private MailSender mailSender;
    private CompletingTestService completingTestService;

    @Autowired
    public UserController(TestService testService, UserService userService,
                          MailSender mailSender, CompletingTestService completingTestService) {
        this.testService = testService;
        this.userService = userService;
        this.mailSender = mailSender;
        this.completingTestService = completingTestService;
    }

    @GetMapping( "/available_tests")
    public String showAvailableTests(String category, Model model){
        UserDetails user = (UserDetails) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        try {

            model.addAttribute("category", Category.valueOf(category));
            Set<Test> tests = testService.getAvailableTestsByUser(userService.findByEmail(user.getUsername()).get())
                    .stream()
                    .filter(test -> test.getCategory().toString().equals(category)) //TODO
                    .filter(test -> test.isActive())
                    .collect(Collectors.toSet());
            model.addAttribute("availableTests", tests);

        }catch (Exception e){
            log.error("Exception ", e);
        }
        return "available_tests";
    }

    @GetMapping( "/completed_tests")
    public String showCompletedTests( Model model){
        UserDetails userDetails = (UserDetails) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        User user =userService.findByEmail(userDetails.getUsername()).get();

        model.addAttribute("success", String.format("%.2f", user.getSuccess())+"%");
        model.addAttribute("tests",testService.setResultsOfTestsForPrintingForCurrentUser(userDetails));

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
            completingTestService.completeTest(userDetails, test);
        }catch (Exception e){
            log.info("Error");
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
        } catch (EntityNotFoundException e) {
            log.error("No test");
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
            mailSender.sendResult(userDetails, test);
        }catch (Exception e){
            return "test_over";
        }
        return "redirect:/";
    }
}
