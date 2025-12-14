package com.incubyte.sweetshop.service;

import com.incubyte.sweetshop.dto.AuthResponse;
import com.incubyte.sweetshop.dto.LoginRequest;
import com.incubyte.sweetshop.dto.RegisterRequest;
import com.incubyte.sweetshop.entity.User;
import com.incubyte.sweetshop.repository.UserRepository;
import com.incubyte.sweetshop.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test suite for AuthService class.
 * Tests user registration and login workflows including validation, JWT token generation,
 * and password encoding. Uses Mockito for dependency injection of repository, password encoder,
 * and JWT utility classes.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    /**
     * Setup method executed before each test.
     * Initializes test data for registration and login requests, as well as a sample user entity.
     * This ensures a consistent baseline for all test cases and eliminates code duplication.
     * 
     * Arrange Phase: Prepares all necessary test objects used in test methods.
     */
    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(User.Role.USER)
                .build();
    }

    /**
     * Test case: Register a new user successfully
     * Scenario: User provides valid registration credentials (username, email, password) that don't already exist
     * 
     * Arrange: Mock repository to return false for existing username/email checks, 
     *          mock password encoder to return encoded password, 
     *          mock JWT util to generate token
     * 
     * Act: Call authService.register() with valid registerRequest
     * 
     * Assert: Verify response contains correct token, username, email, and role (USER by default)
     *         Verify repository.save() and passwordEncoder.encode() were called exactly once
     */
    @Test
    void testRegister_Success() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(user.getUsername())).thenReturn("testToken");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("testToken", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(User.Role.USER, response.getRole());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(registerRequest.getPassword());
    }

    /**
     * Test case: Register fails when username already exists
     * Scenario: User attempts to register with a username that is already in the system
     * 
     * Arrange: Mock repository to return true when checking if username exists
     * 
     * Act: Call authService.register() with duplicate username
     * 
     * Assert: RuntimeException is thrown; verify that user is NEVER saved to repository
     *         Edge case validation: Duplicate username prevention
     */
    @Test
    void testRegister_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test case: Register fails when email already exists
     * Scenario: User attempts to register with an email that is already associated with an account
     * 
     * Arrange: Mock repository to return false for username check (pass first validation),
     *          then return true for email check (fail on second validation)
     * 
     * Act: Call authService.register() with duplicate email
     * 
     * Assert: RuntimeException is thrown; verify that user is NEVER saved to repository
     *         Edge case validation: Duplicate email prevention (enforces unique email constraint)
     */
    @Test
    void testRegister_EmailAlreadyExists() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test case: Login succeeds with valid username and password
     * Scenario: User provides correct login credentials (username and password match stored values)
     * 
     * Arrange: Mock repository to find user by username, mock password encoder to validate password
     *          (returns true for password match), mock JWT util to generate authentication token
     * 
     * Act: Call authService.login() with valid loginRequest
     * 
     * Assert: Verify response contains JWT token and username; confirm password matching was performed
     *         Happy path validation: Successful authentication flow
     */
    @Test
    void testLogin_Success() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getUsername())).thenReturn("testToken");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("testToken", response.getToken());
        assertEquals("testuser", response.getUsername());
        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
    }

    /**
     * Test case: Login fails when username does not exist
     * Scenario: User attempts to login with a username that is not registered in the system
     * 
     * Arrange: Mock repository to return empty Optional when searching for username
     *          (user not found in database)
     * 
     * Act: Call authService.login() with non-existent username
     * 
     * Assert: RuntimeException is thrown; verify that password encoder is NEVER invoked
     *         Security validation: Early exit prevents unnecessary password checking
     */
    @Test
    void testLogin_InvalidUsername() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    /**
     * Test case: Login fails when password is incorrect
     * Scenario: User provides correct username but incorrect password
     * 
     * Arrange: Mock repository to find user by username (user exists), 
     *          mock password encoder to return false (password does not match stored password)
     * 
     * Act: Call authService.login() with valid username but incorrect password
     * 
     * Assert: RuntimeException is thrown preventing unauthorized access
     *         Security validation: Password mismatch detection and rejection
     */
    @Test
    void testLogin_InvalidPassword() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }
}

