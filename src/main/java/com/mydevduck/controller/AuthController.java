package com.mydevduck.controller;

import com.mydevduck.dto.request.LoginRequest;
import com.mydevduck.dto.request.RefreshRequest;
import com.mydevduck.dto.request.RegisterRequest;
import com.mydevduck.dto.response.AuthResponse;
import com.mydevduck.dto.response.UserDTO;
import com.mydevduck.model.User;
import com.mydevduck.model.UserRole;
import com.mydevduck.repository.UserRepository;
import com.mydevduck.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh (@Valid @RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token.");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        User user = userOptional.get();

        String token = jwtTokenProvider.generateAccessToken(userId, user.getEmail(), user.getRole().toString());

        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getGithubUsername(),
                user.getRole(),
                user.getCreatedAt());
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userDTO)
                .tokenType("Bearer")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty() ||
            !passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User user = userOptional.get();
        String token = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getGithubUsername(),
                user.getRole(),
                user.getCreatedAt());
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userDTO)
                .tokenType("Bearer")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register (@Valid
            @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setGithubUsername(request.getGithubUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user = userRepository.save(user);


        String token = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getGithubUsername(),
                user.getRole(),
                user.getCreatedAt());
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userDTO)
                .tokenType("Bearer")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(token);
        String email = jwtTokenProvider.getEmailFromToken(token);
        String role = jwtTokenProvider.getRoleFromToken(token);

        // TODO: Step 4 - Build response map
        //  - Create a Map<String, Object>
        //  - Put: "valid" → true
        //  - Put: "userId" → userId
        //  - Put: "email" → email
        //  - Put: "role" → role
        //  - Put: "message" → "Token is valid"
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("userId", userId);
        response.put("email", email);
        response.put("role", role);
        response.put("message", "Token is valid");

        return ResponseEntity.ok(response);
    }


    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UUID userId) {

        // NOTE: The @AuthenticationPrincipal annotation automatically extracts the userId
        // from the SecurityContext (which was set by JwtAuthenticationFilter)
        // If no valid JWT was provided, userId will be null

        // Step 1 - Check if user is authenticated
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Not authenticated"
            );
        }

        // Step 2 - Query database for user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        // Step 3 - Convert User entity to UserDTO
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getGithubUsername(),
                user.getRole(),
                user.getCreatedAt()
        );

        // Step 4 - Return response
        return ResponseEntity.ok(userDTO);
    }
}
