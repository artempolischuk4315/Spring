package ua.polischuk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(enableDefaultTransactions = true)
public class Application {
    public static void main(String[] args) throws Exception{
        SpringApplication.run(Application.class,args);
    }



}
