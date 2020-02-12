package ua.polischuk.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.polischuk.entity.Test;
import ua.polischuk.entity.User;
import ua.polischuk.repository.TestRepository;
import ua.polischuk.repository.UserRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestService {

    private final TestRepository testRepository;
    private final UserRepository userRepository;

    @Autowired
    public TestService(TestRepository testRepository, UserRepository userRepository, UserRepository userRepository1) {
        this.testRepository = testRepository;
        this.userRepository = userRepository1;
    }

    public Test findTestByName(String name) throws EntityNotFoundException {
        Test test = testRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        return test;
    }

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    public void saveNewTest (Test test){
            test.setActive(true);
            try {
                testRepository.save(test);
            }catch (Exception e){
                throw new EntityExistsException();
            }

    }

    public void disableTest(Test test){
        test.setActive(false);
        testRepository.save(test);
    }

    public void enableTest(Test test) {
        test.setActive(true);
        testRepository.save(test);
    }

    @Transactional
    public Set<Test> addTestToAvailableByEmailAndNameOfTest(String email, String testName) throws EntityNotFoundException {

        Test test = testRepository.findByName(testName).orElseThrow(() -> new EntityNotFoundException(testName));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException((email)));
        if(user.getAvailableTests().isEmpty()) {

            user.setAvailableTests(new HashSet<>(Collections.singleton(test)));

        }
        else {
            Optional.of(user.getAvailableTests()).ifPresent(tests -> tests.add(test));
        }
        userRepository.save(user);
        return user.getAvailableTests();
    }


    public Set<Test> getAvailableTestsByUser(User user) {
        return user.getAvailableTests();
    }

    public Set<Test> setResultsOfTestsForPrintingForCurrentUser(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).get();

        return user.getResultsOfTests().keySet()
                .stream()
                .peek(test -> test.setResult(user.getResultsOfTests().get(test)))
                .collect(Collectors.toSet());

    }
}
