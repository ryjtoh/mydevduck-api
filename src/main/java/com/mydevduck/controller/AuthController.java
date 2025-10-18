package com.mydevduck.controller;

import com.mydevduck.dto.request.LoginRequest;
import com.mydevduck.dto.request.RefreshRequest;
import com.mydevduck.dto.request.RegisterRequest;
import com.mydevduck.dto.response.AuthResponse;
import com.mydevduck.dto.response.UserDTO;
import com.mydevduck.security.JwtTokenProvider;
import com.mydevduck.service.AuthService;
import jakarta.validation.Valid;
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

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh (@Valid @RequestBody RefreshRequest request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register (@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
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
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        UserDTO userDTO = authService.getCurrentUser(userId);
        return ResponseEntity.ok(userDTO);
    }
}
