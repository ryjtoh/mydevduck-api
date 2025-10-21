package com.mydevduck.repository;

import com.mydevduck.model.Pet;
import com.mydevduck.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<Pet, UUID> {

    List<Pet> findByUser(User user);

    boolean existsByUserAndName(User user, String name);

    long countByUser(User user);

    Optional<Pet> findByIdAndUser(UUID id, User user);
}
