package com.al7afez.al7afez.controllers;

import com.al7afez.al7afez.dto.LoginRequest;
import com.al7afez.al7afez.dto.LoginResponse;
import com.al7afez.al7afez.infra.JwtUtil;
import com.al7afez.al7afez.infra.Messages;
import com.al7afez.al7afez.model.entities.AppUser;
import com.al7afez.al7afez.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, Messages.get("error.auth.invalidCredentials"));
        }
        AppUser user = userRepository.findByUsername(request.username()).orElseThrow();
        return new LoginResponse(
                jwtUtil.generate(request.username()),
                user.getId(),
                user.getName(),
                user.getUsername()
        );
    }
}
