package com.educandoweb.course.config;


import com.educandoweb.course.entities.User;
import com.educandoweb.course.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Configuration

@Profile("test")
public class TestConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;


    @Override
    public void run(String... args) throws Exception {
        User user1 = new User(null, "Ruan Lopes", "ruan@gmail.com", "99929999", "129292");
        User user2 = new User(null, "Maria Agostina", "maria@gmail.com", "292828", "111222");

        userRepository.saveAll(Arrays.asList(user1, user2));

    }
}
