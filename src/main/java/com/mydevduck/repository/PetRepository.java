package com.mydevduck.repository;

import com.mydevduck.model.Pet;
import com.mydevduck.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<Pet, UUID> {

    List<Pet> findByUser(User user);

    boolean existsByUserAndName(User user, String name);

    long countByUser(User user);

    Optional<Pet> findByIdAndUser(UUID id, User user);

    Page<Pet> findByIsDeadFalse(Pageable pageable);

    @Query("SELECT p FROM Pet p WHERE p.user = :user AND p.isDead = false AND (p.hunger < 30 OR p.happiness < 30 OR p.health < 30)")
    List<Pet> findPetsNeedingAttention(User user);

}
