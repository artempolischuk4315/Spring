package ua.polischuk.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.polischuk.repository.TestRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestServiceTest {
    @Mock
    TestRepository testRepository;

    @InjectMocks
    TestService testService;


    @Test
    public void shouldReturnSavedTest(){
        ua.polischuk.entity.Test test = new ua.polischuk.entity.Test();
        test.setName("name");

        when(testRepository.save(any(ua.polischuk.entity.Test.class))).thenReturn(new ua.polischuk.entity.Test());

        ua.polischuk.entity.Test newTest = testRepository.save(test);
    }
}
