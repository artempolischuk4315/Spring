package ua.polischuk.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import ua.polischuk.entity.User;
import ua.polischuk.entity.enumsAndRegex.Category;
import ua.polischuk.entity.enumsAndRegex.Role;
import ua.polischuk.repository.TestRepository;
import ua.polischuk.repository.UserRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestServiceTest {
    @Mock
    TestRepository testRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    TestService testService;

    @Mock
    private ua.polischuk.entity.Test test= new ua.polischuk.entity.Test();

    @Mock
    private User user= new User();

    @Before
    public void setUp() {
        when(test.getName()).thenReturn("name");

        when(testRepository.findByName(test.getName())).thenReturn(Optional.of(test));


    }

    @Test
    public void shouldReturnTestsAndResults(){
        UserDetails userDetails = new org.springframework.security.core.userdetails.User
                ("email","pass", Collections.singleton(Role.ROLE_USER));
        User user = new User();
        user.setEmail("email");

        ua.polischuk.entity.Test test= new ua.polischuk.entity.Test();
        test.setName("name");
        test.setCategory(Category.MATH);

        ua.polischuk.entity.Test test1= new ua.polischuk.entity.Test();
        test.setName("name1");
        test.setCategory(Category.HISTORY);

        user.setResultsOfTests(new HashMap<>());
        user.getResultsOfTests().put(test,50);
        user.getResultsOfTests().put(test1, 100);
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));

        Set<ua.polischuk.entity.Test> result = testService.setResultsOfTestsForPrintingForCurrentUser(userDetails);

        assertTrue(result.contains(test));
        assertTrue(result.contains(test1));
        assertThat(test.getResult()).isEqualTo(50);
        assertThat(test1.getResult()).isEqualTo(100);

    }

    @Test
    public void shouldReturnAllUsers(){
        when(testRepository.findAll()).thenReturn(Collections.singletonList(test));
        List<ua.polischuk.entity.Test> result = testService.getAllTests();
        assertThat(result).isEqualTo((Collections.singletonList(test)));
    }

    @Test
    public void shouldAddTestToAvailableIfAvailableTestsEmpty(){
        User user = new User();
        ua.polischuk.entity.Test test= new ua.polischuk.entity.Test();
        test.setName("name");
        test.setCategory(Category.MATH);

        user.setEmail("email");
        user.setAvailableTests(Collections.EMPTY_SET);

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
        when(testRepository.findByName("name")).thenReturn(Optional.of(test));

        Set<ua.polischuk.entity.Test> tests = testService.addTestToAvailableByEmailAndNameOfTest("email", "name");

        assertTrue(tests.contains(test));
    }

    @Test
    public void shouldAddTestToAvailableIfAvailableTestsNotEmpty(){
        User user = new User();

        ua.polischuk.entity.Test testThatAlreadyAdded= new ua.polischuk.entity.Test();
        testThatAlreadyAdded.setName("name0");

        ua.polischuk.entity.Test test= new ua.polischuk.entity.Test();
        test.setName("name");
        test.setCategory(Category.MATH);


        user.setEmail("email");
        user.setAvailableTests(new HashSet<>(Collections.singleton(testThatAlreadyAdded)));


        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
        when(testRepository.findByName("name")).thenReturn(Optional.of(test));


        Set<ua.polischuk.entity.Test> tests = testService.addTestToAvailableByEmailAndNameOfTest("email", "name");

        assertTrue(tests.size()==2);
        assertTrue(tests.contains(test));
    }

    @Test
    public void shouldSetActiveFalseWhenDisableTest(){
        ua.polischuk.entity.Test test= new ua.polischuk.entity.Test();
        test.setActive(true);
        testService.disableTest(test);
        assertThat(false).isEqualTo(test.isActive());
    }

    @Test
    public void shouldSetActiveTrueWhenEnableTest(){
        ua.polischuk.entity.Test test= new ua.polischuk.entity.Test();
        test.setActive(false);
        when(testRepository.save(test)).thenReturn(test);
        testService.enableTest(test);
        assertThat(true).isEqualTo(test.isActive());
    }


    @Test
    public void shouldNotThrowExceptionWhenTestExists(){
        testService.saveNewTest(test);
    }

    @Test(expected = EntityExistsException.class)
    public void shouldThrowExceptionWhenTestExists(){
        when(testRepository.save(test)).thenThrow(new EntityExistsException());
        testService.saveNewTest(test);
    }


    @Test
    public void shouldReturnTestWhenNameCorrect(){
        ua.polischuk.entity.Test result = testService.findTestByName(test.getName());
        assertThat(result).isSameAs(test);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenNameWrong(){
         testService.findTestByName("AnotherName");
    }

    @Test
    public void shouldReturnAvailableTestsByUser(){
        when(user.getAvailableTests()).thenReturn(Collections.emptySet());
        Set<ua.polischuk.entity.Test>  tests = testService.getAvailableTestsByUser(user);
        assertThat(tests).isSameAs(Collections.EMPTY_SET);
    }


}
