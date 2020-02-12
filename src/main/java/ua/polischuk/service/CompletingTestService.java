package ua.polischuk.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.polischuk.entity.Test;
import ua.polischuk.entity.User;
import ua.polischuk.repository.TestRepository;
import ua.polischuk.repository.UserRepository;
import javax.transaction.Transactional;

@Slf4j
@Service
public class CompletingTestService {

    private final static int MIN = 1;

    private final static int MAX = 100;

    private final UserRepository userRepository;

    private final TestRepository testRepository;

    @Autowired
    public CompletingTestService(UserRepository userRepository, TestRepository testRepository) {

        this.userRepository = userRepository;
        this.testRepository = testRepository;
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
        if(user.getAvailableTests().contains(test)&&
                testRepository.findByName(test.getName()).get().isActive()){
            user.getAvailableTests().remove(test);
            userRepository.save(user);
        }
        else throw new Exception();

    }
    private int setRandomResult() {
        return   (MIN+ (int) (Math.random()*MAX));
    }

    private void addTestToCompleted(Test test, User user, Integer result) throws Exception {
        log.info(user.toString());
        removeIfTestCompletedEarlier(test, user);

        log.info(result.toString());

        user.getResultsOfTests().put(testRepository.findByName(test.getName()).orElseThrow(Exception::new), result);//CHANGED

    }

    private boolean removeIfTestCompletedEarlier(Test test, User user){
        //Test testThatWeWantToRemoveFromDB = testRepository.findByName(test.getName()).get();

        if(user.getResultsOfTests().containsKey(test)){
            user.getResultsOfTests().remove(test,
                    user.getResultsOfTests().get(test));
            return true;
        }
        return false;
    }

    private void setStats(User user, Integer result) {

        if(!setSuccessIfEmpty(user, result)) {
            recountSuccessByUser(user);
        }
    }

    protected void recountSuccessByUser(User user) {
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
}
