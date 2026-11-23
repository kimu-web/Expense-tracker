package com.example.expensetracker.controller;

import com.example.expensetracker.dto.RegisterRequest;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is already taken"));
        }

        User user = new User(request.username(), passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully", "username", user.getUsername()));
    }
}
