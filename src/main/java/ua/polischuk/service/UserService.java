package ua.polischuk.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ua.polischuk.dto.UsersDTO;
import ua.polischuk.entity.Test;
import ua.polischuk.entity.User;
import ua.polischuk.entity.enumsAndRegex.Role;
import ua.polischuk.repository.TestRepository;
import ua.polischuk.repository.UserRepository;
import ua.polischuk.service.constants.Admin_Data;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private final static int MAX = 100;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private MailSender mailSender;

    @Autowired
    public UserService(UserRepository userRepository, TestRepository testRepository, MailSender mailSender) {
        this.userRepository = userRepository;
        this.testRepository = testRepository;
        this.mailSender = mailSender;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException(email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), Collections.singleton(user.getRoles()));
    }

    public UsersDTO getAllUsers() {
        return new UsersDTO(userRepository.findAll());
    }

    public Set<Test> getAvailableTests(User user) {
        return user.getAvailableTests();
    }

    @Transactional
    public void addTestToAvailable(String email, String testName) throws EntityNotFoundException {

        Test test = testRepository.findByName(testName).orElseThrow(() -> new EntityNotFoundException(testName));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException((email)));
        if(user.getAvailableTests().isEmpty()) {
            setAvailableTestsForUser(user, test);
        }
        else {
            Optional.of(user.getAvailableTests()).ifPresent(tests -> tests.add(test));
        }
        userRepository.save(user);
    }

    private void setAvailableTestsForUser(User user, Test test) {
        Set<Test> t = new HashSet<>();
        t.add(test);
        user.setAvailableTests(t);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void recountSuccessOnMainUserPage(UserDetails userDetails){
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
        recountSuccess(user.get());
        userRepository.save(user.get());
    }

    public User saveNewUser(User user) throws EntityExistsException {
        User savedUser;
        setParametersOfNewUser(user);
        try {
            savedUser = userRepository.save(user);
        }catch (Exception e){
            throw new EntityExistsException();
        }
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    Admin_Data.EMAIL_HELLO,
                    user.getEmail(),
                    user.getActivationCode()
            );

            mailSender.send(user.getEmail(), Admin_Data.ACTIVATION_CODE, message);
        }
        return savedUser;
    }

    private void setParametersOfNewUser(User user) {

        user.setRoles(Role.ROLE_USER);
        user.setFirstName(user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1).toLowerCase());
        user.setLastName(user.getLastName().substring(0, 1).toUpperCase() + user.getLastName().substring(1).toLowerCase());
        user.setFirstName_ru(user.getFirstName_ru().substring(0, 1).toUpperCase() + user.getFirstName_ru().substring(1).toLowerCase());
        user.setLastName_ru(user.getLastName_ru().substring(0, 1).toUpperCase() + user.getLastName_ru().substring(1).toLowerCase());

        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

    }

    public Set<Test> setResultsOfTestsForPrinting(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).get();
        Set<Test> tests = user.getResultsOfTests().keySet();
        tests.stream()
                .forEach(test -> test.setResult(user.getResultsOfTests().get(test)));
        return tests;
    }

    @Transactional
    public boolean activateUser(String code) {
        if( !userRepository.findByActivationCode(code).isPresent()){
            return false;
        }
        User user = userRepository.findByActivationCode(code).get();
        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);

        return true;
    }

    public void manageResultAndCompleteTest(UserDetails userDetails, Test test) throws Exception {
        completeTest(userDetails, test);
    }


    @Transactional
    public void completeTest(UserDetails userDetails, Test test) throws Exception {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(Exception::new); //запрос в БД на текущего пользователя
        dropTestFromAvailable(test, user);
        Integer result = setRandomResult();
        addTestToCompleted(test, user, result);
        setStats(user, result);
        userRepository.save(user);
    }



   private void dropTestFromAvailable(Test test, User user) throws Exception {
        if(user.getAvailableTests().contains(testRepository.findByName(test.getName()).get())&
                testRepository.findByName(test.getName()).get().isActive()){
            user.getAvailableTests().remove(testRepository.findByName(test.getName()).orElseThrow(Exception::new));
            userRepository.save(user);
        }
       else throw new Exception();

    }
    private int setRandomResult() {
        return   (1+ (int) (Math.random()*MAX));
    }

    private void addTestToCompleted(Test test, User user, Integer result) throws Exception {
        log.info(user.toString());
        removeIfTestCompletedEarlier(test, user);

        log.info(result.toString());

        if(!Optional.of(user.getResultsOfTests().keySet()).isPresent()){
            user.setResultsOfTests(
                    (HashMap<Test, Integer>) new HashMap<>().put(testRepository.findByName(test.getName()), result));
        }
        else{
            user.getResultsOfTests().put(testRepository.findByName(test.getName()).orElseThrow(Exception::new), result);
        }

    }

    private boolean removeIfTestCompletedEarlier(Test test, User user){
        Test testThatWeWantToRemoveFromDB = testRepository.findByName(test.getName()).get();

        if(user.getResultsOfTests().containsKey(testThatWeWantToRemoveFromDB)){
            user.getResultsOfTests().remove(testThatWeWantToRemoveFromDB,
                    user.getResultsOfTests().get(testThatWeWantToRemoveFromDB));

            return true;
        }
        return false;
    }

    private void setStats(User user, Integer result) {

        if(!setSuccessIfEmpty(user, result)) {
            recountSuccess(user);
        }
    }

    private void recountSuccess(User user) {
        log.info(user.toString());
        if( user.getResultsOfTests().keySet().isEmpty()){
            user.setSuccess(0.0);
        }else{
        Integer size = user.getResultsOfTests().keySet().size();

        double sumResults = user.getResultsOfTests().keySet().stream()
                .mapToInt(test -> user.getResultsOfTests().get(test))
                .sum();
        user.setSuccess(sumResults/size);
        }
    }

    private boolean setSuccessIfEmpty(User user, int result) {
        if(user.getSuccess() == 0) {
            user.setSuccess(result);
            return true;
        }
        return false;
    }

    public void sendResult(UserDetails userDetails, Test test){
        String email = userDetails.getUsername();
        User user =userRepository.findByEmail(email).get();
        //getUserAndTest(user, test, email);
        Integer result = user.getResultsOfTests().get(test);
        mailSender.send(email, Admin_Data.EMAIL, Admin_Data.MAIL_MESSAGE+ test.getName()+Admin_Data.IS+result.toString());

    }

}
