package com.library.management.controller;

import com.library.management.dto.UserResponse;
import com.library.management.entity.User;
import com.library.management.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==========================================
    // GET ALL USERS
    // ==========================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users =
                userService.getAllUsers()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(users);
    }

    // ==========================================
    // GET USER BY ID
    // ==========================================

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ) {

        User user = userService.getUserById(id);

        return ResponseEntity.ok(
                toResponse(user)
        );
    }

    // ==========================================
    // CREATE USER
    // ==========================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody User user
    ) {

        User savedUser =
                userService.createUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedUser));
    }

    // ==========================================
    // UPDATE USER
    // ==========================================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user
    ) {

        User updatedUser =
                userService.updateUser(
                        id,
                        user
                );

        return ResponseEntity.ok(
                toResponse(updatedUser)
        );
    }

    // ==========================================
    // CHANGE PASSWORD
    // ==========================================

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {

        String newPassword =
                request.get("password");

        userService.changePassword(
                id,
                newPassword
        );

        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }

    // ==========================================
    // DELETE USER
    // ==========================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id
    ) {

        userService.deleteUser(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // ==========================================
    // GET USER BY EMAIL
    // ==========================================

    @GetMapping("/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByEmail(
            @RequestParam String email
    ) {

        User user =
                userService.getUserByEmail(email);

        return ResponseEntity.ok(
                toResponse(user)
        );
    }

    // ==========================================
    // ENTITY → DTO
    // ==========================================

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}