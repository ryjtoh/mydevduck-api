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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user with email and password
     * @param request RegisterRequest containing email, password, and optional githubUsername
     * @return AuthResponse with JWT tokens and user info
     * @throws ResponseStatusException 409 if email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicate email
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
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid credentials"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

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
