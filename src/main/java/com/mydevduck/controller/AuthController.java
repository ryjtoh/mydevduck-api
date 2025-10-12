package com.mydevduck.controller;

import com.mydevduck.dto.response.UserDTO;
import com.mydevduck.model.User;
import com.mydevduck.repository.UserRepository;
import com.mydevduck.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * POST /api/v1/auth/validate
     *
     * Purpose: Validate a JWT token and return user info from the token claims
     *
     * Request: POST with Authorization header
     * Header: Authorization: Bearer <jwt-token>
     *
     * Response (Success - 200):
     * {
     *   "valid": true,
     *   "userId": "550e8400-e29b-41d4-a716-446655440000",
     *   "email": "user@example.com",
     *   "role": "USER",
     *   "message": "Token is valid"
     * }
     *
     * Response (Error - 401):
     * {
     *   "timestamp": "...",
     *   "status": 401,
     *   "error": "Unauthorized",
     *   "message": "Invalid or expired token"
     * }
     */
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

    /**
     * GET /api/v1/auth/me
     *
     * Purpose: Get current authenticated user's full information from database
     *
     * How it works:
     * 1. JwtAuthenticationFilter automatically validates the JWT token (BEFORE this method runs)
     * 2. If token is valid, filter extracts userId and sets it in SecurityContext
     * 3. @AuthenticationPrincipal automatically injects the userId into this method parameter
     * 4. We use that userId to look up the user in the database
     * 5. Return the full user details
     *
     * Request: GET with Authorization header
     * Header: Authorization: Bearer <jwt-token>
     *
     * Response (Success - 200):
     * {
     *   "id": "550e8400-e29b-41d4-a716-446655440000",
     *   "email": "user@example.com",
     *   "githubUsername": "johndoe",
     *   "role": "USER",
     *   "createdAt": "2025-10-01T10:30:00"
     * }
     *
     * Response (Error - 401):
     * User not authenticated (no valid JWT token)
     *
     * Response (Error - 404):
     * User ID from token doesn't exist in database
     */
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
