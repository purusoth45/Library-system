package com.library.management.controller;

import com.library.management.dto.LoginRequest;
import com.library.management.dto.LoginResponse;
import com.library.management.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        System.out.println(">>> AUTH LOGIN CONTROLLER CALLED");

        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}