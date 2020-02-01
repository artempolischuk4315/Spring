package ua.polischuk.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.polischuk.dto.TestsDTO;
import ua.polischuk.entity.Test;
import ua.polischuk.repository.TestRepository;
import ua.polischuk.repository.UserRepository;

import javax.transaction.Transactional;

@Slf4j
@Service
public class TestService {

    private final TestRepository testRepository;
    private final UserRepository userRepository;

    @Autowired
    public TestService(TestRepository testRepository, UserRepository userRepository) {
        this.testRepository = testRepository;
        this.userRepository = userRepository;
    }

    public Test findTestByName(String name) throws Exception {
        Test test = testRepository.findByName(name).orElseThrow(Exception::new);
        return test;
    }

    public TestsDTO getAllTests() {
        //TODO checking for an empty test list

        return new TestsDTO(testRepository.findAll());
    }


    @Transactional
    public void saveNewTest (Test test) throws Exception{
            test.setActive(true);
            testRepository.save(test);
    }

    @Transactional
    public void deleteTest (Test test) throws Exception{
        /*test.setActive(false);
        testRepository.save(test);*/
        testRepository.delete(test);
    }

    public void disableTest(Test test){
        test.setActive(false);
        testRepository.save(test);
    }

    public void enableTest(Test test) {
        test.setActive(true);
        testRepository.save(test);
    }
}
