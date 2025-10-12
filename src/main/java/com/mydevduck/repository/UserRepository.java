package com.mydevduck.repository;

import com.mydevduck.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByGithubUsername(String githubUsername);

    boolean existsByEmail(String email);

    boolean existsByGithubUsername(String githubUsername);
}
