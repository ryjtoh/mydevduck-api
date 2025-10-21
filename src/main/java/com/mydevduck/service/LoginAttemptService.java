package com.mydevduck.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service to track and prevent brute force login attempts.
 * Uses Redis to store failed login attempts with TTL.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);
    private static final String ATTEMPT_PREFIX = "login:attempt:";
    private static final String LOCKOUT_PREFIX = "login:lockout:";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Records a failed login attempt for the given identifier (email or IP)
     * @param identifier The email or IP address
     */
    public void loginFailed(String identifier) {
        String key = ATTEMPT_PREFIX + identifier;

        // Increment attempt counter
        Long attempts = redisTemplate.opsForValue().increment(key);

        if (attempts == 1) {
            // Set TTL on first attempt
            redisTemplate.expire(key, LOCKOUT_DURATION);
        }

        // If max attempts reached, set lockout flag
        if (attempts >= MAX_ATTEMPTS) {
            String lockoutKey = LOCKOUT_PREFIX + identifier;
            redisTemplate.opsForValue().set(lockoutKey, "locked", LOCKOUT_DURATION);
            log.warn("Account locked due to {} failed login attempts: {}", attempts, identifier);
        }

        log.debug("Failed login attempt {} for: {}", attempts, identifier);
    }

    /**
     * Checks if the identifier is currently locked out due to too many failed attempts
     * @param identifier The email or IP address
     * @return true if locked out, false otherwise
     */
    public boolean isBlocked(String identifier) {
        String lockoutKey = LOCKOUT_PREFIX + identifier;
        Boolean locked = redisTemplate.hasKey(lockoutKey);
        return Boolean.TRUE.equals(locked);
    }

    /**
     * Resets the failed login attempt counter for the identifier
     * Called after successful login
     * @param identifier The email or IP address
     */
    public void loginSucceeded(String identifier) {
        String attemptKey = ATTEMPT_PREFIX + identifier;
        String lockoutKey = LOCKOUT_PREFIX + identifier;

        redisTemplate.delete(attemptKey);
        redisTemplate.delete(lockoutKey);

        log.debug("Login attempt counter reset for: {}", identifier);
    }

    /**
     * Gets the remaining attempts before lockout
     * @param identifier The email or IP address
     * @return Number of remaining attempts
     */
    public int getRemainingAttempts(String identifier) {
        String key = ATTEMPT_PREFIX + identifier;
        String attemptsStr = redisTemplate.opsForValue().get(key);

        if (attemptsStr == null) {
            return MAX_ATTEMPTS;
        }

        int attempts = Integer.parseInt(attemptsStr);
        return Math.max(0, MAX_ATTEMPTS - attempts);
    }
}
