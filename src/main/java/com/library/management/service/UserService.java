package com.library.management.service;

import com.library.management.entity.Role;
import com.library.management.entity.User;
import com.library.management.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // GET ALL USERS
    // ==========================================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ==========================================
    // GET USER BY ID
    // ==========================================

    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + id
                        )
                );
    }

    // ==========================================
    // CREATE USER
    // ==========================================

    public User createUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException(
                    "User already exists with email: "
                            + user.getEmail()
            );
        }

        // Default role
        if (user.getRole() == null) {
            user.setRole(Role.MEMBER);
        }

        // Password validation
        if (user.getPassword() == null
                || user.getPassword().isBlank()) {

            throw new RuntimeException(
                    "Password is required"
            );
        }

        // Encode password before saving
        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        return userRepository.save(user);
    }

    // ==========================================
    // UPDATE USER
    // ==========================================

    public User updateUser(
            Long id,
            User userDetails
    ) {

        User user = getUserById(id);

        // Check duplicate email
        if (!user.getEmail().equals(userDetails.getEmail())
                && userRepository.existsByEmail(
                userDetails.getEmail()
        )) {

            throw new RuntimeException(
                    "Email already exists: "
                            + userDetails.getEmail()
            );
        }

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());

        // Update password only when provided
        if (userDetails.getPassword() != null
                && !userDetails.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(
                            userDetails.getPassword()
                    )
            );
        }

        // Update role
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }

        return userRepository.save(user);
    }

    // ==========================================
    // CHANGE PASSWORD
    // ==========================================

    public void changePassword(
            Long id,
            String newPassword
    ) {

        User user = getUserById(id);

        if (newPassword == null
                || newPassword.isBlank()) {

            throw new RuntimeException(
                    "Password is required"
            );
        }

        // BCrypt encode new password
        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        userRepository.save(user);
    }

    // ==========================================
    // DELETE USER
    // ==========================================

    public void deleteUser(Long id) {

        User user = getUserById(id);

        userRepository.delete(user);
    }

    // ==========================================
    // GET USER BY EMAIL
    // ==========================================

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with email: "
                                        + email
                        )
                );
    }
}