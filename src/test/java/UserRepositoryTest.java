import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.polischuk.Application;
import ua.polischuk.entity.User;
import ua.polischuk.repository.UserRepository;

import lombok.*;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static ua.polischuk.entity.enumsAndRegex.Role.ROLE_USER;

@Setter
@Getter
@Slf4j

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes=Application.class)

public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByEmail_thenReturnUser() {
        User alex = new User("alex","test","mail@mail.com","pass", ROLE_USER);
        entityManager.persist(alex);
        entityManager.flush();

        User found = userRepository.findByEmail(alex.getEmail()).get();

        assertTrue(found.getEmail().equals(alex.getEmail()));
    }


}

