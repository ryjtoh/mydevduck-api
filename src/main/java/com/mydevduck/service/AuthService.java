package com.mydevduck.service;

import com.mydevduck.dto.request.LoginRequest;
import com.mydevduck.dto.request.RegisterRequest;
import com.mydevduck.dto.response.AuthResponse;
import com.mydevduck.dto.response.UserDTO;
import com.mydevduck.model.User;
import com.mydevduck.model.UserRole;
import com.mydevduck.repository.UserRepository;
import com.mydevduck.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

// TODO: Write integration tests for AuthService
//  - Test successful registration, login, token refresh
//  - Test duplicate email registration (409 error)
//  - Test invalid credentials (401 error)
//  - Test expired/invalid refresh tokens
//  - Use @SpringBootTest, TestRestTemplate, and test database (H2)
//  - Create test fixtures for User entities

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginAttemptService loginAttemptService;

    /**
     * Register a new user with email and password
     * @param request RegisterRequest containing email, password, and optional githubUsername
     * @return AuthResponse with JWT tokens and user info
     * @throws ResponseStatusException 409 if email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicate email
        // TODO (OPTIONAL): Create EmailAlreadyExistsException extends RuntimeException for cleaner exception handling
        //  - Create custom exception class in com.mydevduck.exception package
        //  - Add @ControllerAdvice to handle it globally
        //  - Replace ResponseStatusException with custom exception
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        // Create new user with hashed password
        User user = new User();
        user.setEmail(request.getEmail());
        user.setGithubUsername(request.getGithubUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        user = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // Build response
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getGithubUsername(),
                user.getRole(),
                user.getCreatedAt());

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(userDTO)
                .tokenType("Bearer")
                .build();
    }

    /**
     * Login with email and password
     * @param request LoginRequest containing email and password
     * @return AuthResponse with JWT tokens and user info
     * @throws ResponseStatusException 401 if credentials are invalid
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail();

        // Check if account is locked due to too many failed attempts
        if (loginAttemptService.isBlocked(email)) {
            log.warn("Login attempt blocked for locked account: {}", email);
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many failed login attempts. Please try again later.");
        }

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    loginAttemptService.loginFailed(email);
                    return new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid credentials");
                });

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            loginAttemptService.loginFailed(email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Reset failed attempts on successful login
        loginAttemptService.loginSucceeded(email);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // TODO (RECOMMENDED): Store refresh token in database for revocation support
        //  - Create RefreshToken entity with: id, userId, token, expiresAt, createdAt
        //  - Save refresh token to database here
        //  - On logout: delete refresh token from database
        //  - On refresh: verify token exists in database before issuing new access token
        //  - Add scheduled job to clean up expired tokens

        // Build response
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getGithubUsername(),
                user.getRole(),
                user.getCreatedAt());

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(userDTO)
                .tokenType("Bearer")
                .build();
    }

    /**
     * Refresh access token using a valid refresh token
     * @param refreshToken The refresh token
     * @return AuthResponse with new access token and user info
     * @throws ResponseStatusException 401 if refresh token is invalid or user not found
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid or expired refresh token");
        }

        // Extract user ID from token
        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // Fetch user from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"));

        // Generate new access token (keep same refresh token)
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().toString());

        // Build response
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getGithubUsername(),
                user.getRole(),
                user.getCreatedAt());

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .user(userDTO)
                .tokenType("Bearer")
                .build();
    }

    /**
     * Get current user information by user ID
     * @param userId The user's UUID
     * @return UserDTO containing user information
     * @throws ResponseStatusException 404 if user not found
     */
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"));

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getGithubUsername(),
                user.getRole(),
                user.getCreatedAt());
    }
}
