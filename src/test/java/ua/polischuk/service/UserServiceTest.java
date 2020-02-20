package ua.polischuk.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import ua.polischuk.entity.User;
import ua.polischuk.entity.Role;
import ua.polischuk.repository.UserRepository;

import javax.persistence.EntityExistsException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    MailSender mailSender;

    @InjectMocks
    UserService userService;

    @Mock
    private User user;

    @Before
    public void setUp() {
        when(user.getEmail()).thenReturn("email");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(user.getFirstName()).thenReturn("irstName");
        when(user.getFirstName_ru()).thenReturn("ИмЯ");
        when(user.getLastName()).thenReturn("lname");
        when(user.getLastName_ru()).thenReturn("lanmeRu");
        when(user.getPassword()).thenReturn("pass");
        when(user.getActivationCode()).thenReturn("code");
    }


    @Test
    public void shouldReturnUserWhenNameCorrect(){
        User result = userService.findByEmail(user.getEmail()).get();
        assertThat(result).isSameAs(user);
    }

    @Test
    public void shouldReturnEmptyWhenEmailWrong(){
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Optional<User> result = userService.findByEmail("AnotherMail");
        assertThat(result).isEmpty();
    }


    @Test
    public void shouldReturnUserDetails(){
        when(user.getRoles()).thenReturn(Role.ROLE_USER);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User
                ("email","pass", Collections.singleton(Role.ROLE_USER));
        UserDetails newUserDetails = userService.loadUserByUsername("email");
        assertThat(newUserDetails).isEqualTo(userDetails);

    }

    @Test(expected = EntityExistsException.class)
    public void shouldThrowExceptionWhenUserExists(){
        when(userRepository.save(user)).thenThrow(new EntityExistsException());
        userService.saveNewUser(user);
    }

    @Test
    public void shouldReturnSavedUser(){
        when(userRepository.save(user)).thenReturn(user);
        User newUser = userService.saveNewUser(user);

        assertThat(newUser).isSameAs(user);

    }

    @Test
    public void shouldReturnAllUsers(){
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        List<User> result = userService.getAllUsers();
        assertThat(result).isEqualTo((Collections.singletonList(user)));
    }

    @Test
    public void shouldReturnRightParamsOfUser(){
        User user = new User();
        user.setFirstName("firstName");
        user.setFirstName_ru("ИмЯ");
        user.setLastName("LastName");
        user.setLastName_ru("Фамилия");
        user.setEmail("mail@fdf.fkf");
        user.setActivationCode("code");
        user.setPassword("1");

        when(userRepository.save(user)).thenReturn(user);
        User newUser = userService.saveNewUser(user);
        assertEquals("Firstname", newUser.getFirstName()); //check UpperCase
        assertEquals("Имя", newUser.getFirstName_ru());
        assertEquals("ROLE_USER", newUser.getRoles().toString()); //checkRoleSetting

    }

    @Test
    public void shouldReturnActive(){
        User user = new User();
        user.setEmail("mail@fdf.fkf");
        user.setActivationCode("code");
       when(userRepository.findByActivationCode("code")).thenReturn(Optional.of(user));
       userService.activateUser("code");
       assertTrue( user.isActive());

    }

    @Test
    public void shouldReturnFalseWhenCodeIsWrong(){
        User user = new User();
        user.setEmail("mail@fdf.fkf");
        user.setActivationCode("code");
        when(userRepository.findByActivationCode("code")).thenReturn(Optional.empty());
        boolean result = userService.activateUser("code");
        assertFalse(result);
    }

}
