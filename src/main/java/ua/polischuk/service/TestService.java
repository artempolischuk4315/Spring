package ua.polischuk.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.polischuk.dto.TestsDTO;
import ua.polischuk.entity.Test;
import ua.polischuk.repository.TestRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class TestService {

    private final TestRepository testRepository;

    @Autowired
    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public Test findTestByName(String name) throws Exception {
        Test test = testRepository.findByName(name).orElseThrow(Exception::new);
        return test;
    }

    public TestsDTO getAllTests() {
        //TODO checking for an empty test list

        return new TestsDTO(testRepository.findAll());

    }


    public void saveNewTest (Test test) throws Exception{
        testRepository.save(test);
    }

    public void deleteTest (Test test) throws Exception{

        testRepository.delete(test);
    }

}
