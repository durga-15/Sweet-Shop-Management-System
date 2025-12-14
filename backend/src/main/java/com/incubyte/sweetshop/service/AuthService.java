package com.incubyte.sweetshop.service;

import com.incubyte.sweetshop.dto.AuthResponse;
import com.incubyte.sweetshop.dto.LoginRequest;
import com.incubyte.sweetshop.dto.RegisterRequest;
import com.incubyte.sweetshop.entity.User;
import com.incubyte.sweetshop.repository.UserRepository;
import com.incubyte.sweetshop.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AUTH SERVICE - Handles all authentication logic (sign-up and login)
 * 
 * This service does the heavy lifting for authentication:
 * 1. Register: Creates new user accounts and generates login tokens
 * 2. Login: Verifies credentials and generates login tokens
 * 3. Register Admin: Creates admin accounts with special permissions
 * 
 * The token (JWT) is like a ticket that proves the user is logged in.
 * The frontend stores this token and includes it with each request.
 */
@Service  // This tells Spring this class contains business logic
@RequiredArgsConstructor  // Automatically injects dependencies
public class AuthService {
    // Database access for user records
    private final UserRepository userRepository;
    // Used to encrypt passwords so they're never stored in plain text
    private final PasswordEncoder passwordEncoder;
    // Creates JWT tokens for authentication
    private final JwtUtil jwtUtil;

    /**
     * REGISTER - Create a new customer account
     * 
     * Steps:
     * 1. Check if username is already taken
     * 2. Check if email is already registered
     * 3. Create new user with encrypted password
     * 4. Save user to database
     * 5. Generate authentication token
     * 6. Return token and user info
     */
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists in database
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists in database
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user object with encrypted password
        // Password is encrypted (hashed) so it cannot be reversed
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // Encrypt password
                .role(User.Role.USER)  // New users get regular USER role (not admin)
                .build();

        // Save the new user to database
        user = userRepository.save(user);

        // Generate a JWT token - this is sent to frontend for authentication
        String token = jwtUtil.generateToken(user.getUsername());

        // Return token and user info
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole());
    }

    /**
     * LOGIN - Allow users to sign in with their credentials
     * 
     * Steps:
     * 1. Find user by username
     * 2. Verify password matches
     * 3. Generate authentication token
     * 4. Return token and user info
     */
    public AuthResponse login(LoginRequest request) {
        // Find user in database by username
        // If not found, throw error
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Verify the password matches what's stored in database
        // passwordEncoder.matches() safely compares the provided password with encrypted version
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate a JWT token for this login session
        String token = jwtUtil.generateToken(user.getUsername());

        // Return token and user info
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole());
    }

    /**
     * REGISTER ADMIN - Create a new admin/shop manager account
     * 
     * Similar to register() but gives the user ADMIN role instead of USER role.
     * Only someone with admin access should be able to call this.
     */
    public AuthResponse registerAdmin(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new admin user with ADMIN role
        // This is the only difference from regular register()
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // Encrypt password
                .role(User.Role.ADMIN)  // New admin gets ADMIN role (special permissions)
                .build();

        // Save the new admin user to database
        user = userRepository.save(user);

        // Generate a JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // Return token and admin user info
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole());
    }
}

