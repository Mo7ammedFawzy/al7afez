package com.al7afez.al7afez.infra;

import com.al7afez.al7afez.model.entities.AppUser;
import com.al7afez.al7afez.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        AppUser admin = new AppUser();
        admin.setName("مدير النظام");
        admin.setCode("ADMIN");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(admin);

        System.out.println(">>> Default user created — username: admin | password: admin");
    }
}
