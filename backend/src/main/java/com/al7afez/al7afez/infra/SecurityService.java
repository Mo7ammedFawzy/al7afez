package com.al7afez.al7afez.infra;

import com.al7afez.al7afez.model.entities.AppUser;
import com.al7afez.al7afez.model.entities.Sheikh;
import com.al7afez.al7afez.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityService {

    private final UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<Sheikh> getCurrentSheikh() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(username)
                .map(AppUser::getSheikh);
    }
}