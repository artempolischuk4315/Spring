package ua.polischuk.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.polischuk.dto.TestsDTO;
import ua.polischuk.entity.Test;
import ua.polischuk.repository.TestRepository;
import ua.polischuk.repository.UserRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
public class TestService {

    private final TestRepository testRepository;

    @Autowired
    public TestService(TestRepository testRepository, UserRepository userRepository) {
        this.testRepository = testRepository;
    }

    public Test findTestByName(String name) throws EntityNotFoundException { //TODO
        Test test = testRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        return test;
    }

    public TestsDTO getAllTests() {
        return new TestsDTO(testRepository.findAll());
    }


    public void saveNewTest (Test test){
            test.setActive(true);
            try {
                testRepository.save(test);
            }catch (Exception e){
                throw new EntityExistsException();
            }

    }

 /*   @Transactional
    public void deleteTest (Test test) {
        testRepository.delete(test);
    }*/

    public void disableTest(Test test){
        test.setActive(false);
        testRepository.save(test);
    }

    public void enableTest(Test test) {
        test.setActive(true);
        testRepository.save(test);
    }
}
