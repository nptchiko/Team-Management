package com.thehecotnha.backend;

import com.thehecotnha.backend.entity.User;
import com.thehecotnha.backend.enums.Role;
import com.thehecotnha.backend.enums.UserStatus;
import com.thehecotnha.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
