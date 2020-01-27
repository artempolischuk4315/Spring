package ua.polischuk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.polischuk.entity.User;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>  {
   Optional<User> findByEmail(String email);
   Optional<User> findByActivationCode(String activationCode);
}
