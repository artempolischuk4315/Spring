package ua.polischuk.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ua.polischuk.entity.Role;
import ua.polischuk.entity.User;
import ua.polischuk.repository.TestRepository;
import ua.polischuk.repository.UserRepository;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    public static final String ADMIN_MAIL = "admin.mail";
    public static final String ACTIVATION_CODE = "activation.code";
    public static final String EMAIL_HELLO = "email.hello";
    public static final String HELLO_S = "Hello, %s!";
    private final UserRepository userRepository;

    private MailSender mailSender;

    ResourceBundle bundle = ResourceBundle.getBundle("adminAndMail");

    @Autowired
    public UserService(UserRepository userRepository, TestRepository testRepository, MailSender mailSender) {

        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException(email));


        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), Collections.singleton(user.getRoles()));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public User saveNewUser(User user) throws EntityExistsException {
        User savedUser;
        setParametersOfNewUser(user);

        try {
            savedUser = userRepository.save(user);
        }catch (DataIntegrityViolationException e){

            log.error("Saving new user exception", e);

            throw new EntityExistsException("Entity already exists");
        }
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    HELLO_S +
                    bundle.getString(EMAIL_HELLO),
                    user.getEmail(),
                    user.getActivationCode()
            );

            mailSender.send(user.getEmail(),  bundle.getString(ACTIVATION_CODE), message);
        }
        return savedUser;
    }

    private void setParametersOfNewUser(User user) {

        if(user.getEmail().equals( bundle.getString(ADMIN_MAIL))){
            user.setRoles(Role.ROLE_ADMIN);
        }else user.setRoles(Role.ROLE_USER);

        user.setFirstName(user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1).toLowerCase());
        user.setLastName(user.getLastName().substring(0, 1).toUpperCase() + user.getLastName().substring(1).toLowerCase());
        user.setFirstName_ru(user.getFirstName_ru().substring(0, 1).toUpperCase() + user.getFirstName_ru().substring(1).toLowerCase());
        user.setLastName_ru(user.getLastName_ru().substring(0, 1).toUpperCase() + user.getLastName_ru().substring(1).toLowerCase());

        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

    }

    @Transactional
    public boolean activateUser(String code) {
        if(!userRepository.findByActivationCode(code).isPresent()){
            return false;
        }
        User user = userRepository.findByActivationCode(code).get();
        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);

        return true;
    }

}
