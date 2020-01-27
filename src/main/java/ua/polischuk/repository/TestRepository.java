package ua.polischuk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.polischuk.entity.Test;

import java.util.Optional;


@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    Optional<Test> findByName(String name);
}
