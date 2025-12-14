package com.incubyte.sweetshop.controller;

import com.incubyte.sweetshop.dto.AuthResponse;
import com.incubyte.sweetshop.dto.LoginRequest;
import com.incubyte.sweetshop.dto.RegisterRequest;
import com.incubyte.sweetshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AUTHENTICATION CONTROLLER - Handles user sign-up and login
 * 
 * This controller manages all user authentication requests:
 * - Register new users (customers)
 * - Register new admins (shop managers)
 * - Login existing users
 * 
 * All endpoints are under: http://localhost:8081/api/auth
 * Example: http://localhost:8081/api/auth/login
 */
@RestController  // This tells Spring this class handles web requests
@RequestMapping("/api/auth")  // All authentication endpoints start with /api/auth
@RequiredArgsConstructor  // Automatically creates constructor for dependencies
@CrossOrigin(origins = "*")  // Allows frontend to access these endpoints
public class AuthController {
    private final AuthService authService;

    /**
     * REGISTER - Create a new customer account
     * Endpoint: POST /api/auth/register
     * Receives: username, email, password
     * Returns: authentication token and user info
     */
    @PostMapping("/register")  // Responds to POST requests at /api/auth/register
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Call the service to create the new user
        AuthResponse response = authService.register(request);
        // Return 201 (CREATED) status code for successful registration
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * LOGIN - Allow users to sign in with their credentials
     * Endpoint: POST /api/auth/login
     * Receives: username, password
     * Returns: authentication token and user info
     */
    @PostMapping("/login")  // Responds to POST requests at /api/auth/login
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Call the service to verify credentials and generate token
        AuthResponse response = authService.login(request);
        // Return 200 (OK) status code for successful login
        return ResponseEntity.ok(response);
    }

    /**
     * REGISTER ADMIN - Create a new admin/shop manager account
     * Endpoint: POST /api/auth/register-admin
     * Receives: username, email, password
     * Returns: authentication token with ADMIN role and user info
     */
    @PostMapping("/register-admin")  // Responds to POST requests at /api/auth/register-admin
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        // Call the service to create a new admin user
        AuthResponse response = authService.registerAdmin(request);
        // Return 201 (CREATED) status code for successful registration
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

