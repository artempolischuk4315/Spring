package ua.polischuk.service;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import ua.polischuk.dto.UsersDTO;
import ua.polischuk.entity.enumsAndRegex.Role;
import ua.polischuk.entity.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    public void loadUserByUsername() {
        UserDetails user=  userService.loadUserByUsername("test2@mail.ua");
        Assert.assertTrue("test2@mail.ua".equals(user.getUsername()));
    }

    @Test
    public void getAllUsers() {
        //for current size = 9
        UsersDTO usersDTO= userService.getAllUsers();
        int size = usersDTO.getUsers().size();
        Assert.assertTrue(size==9);
    }


    @Test
    public void saveNewUser() throws Exception {
        User user = new User("Tester2","Tester2","test2@mail.ua","pass", Role.ROLE_USER);
        //boolean isUserSaved = userService.saveNewUser(user);
        //Assert.assertTrue(isUserSaved);
    }
    @Test
    public void saveUserThatExists() throws Exception {
        User user = new User("Tester","Tester","test@mail.ua","pass", Role.ROLE_USER);
       // boolean isUserSaved = userService.saveNewUser(user);
       // Assert.assertFalse(isUserSaved);
    }

}
