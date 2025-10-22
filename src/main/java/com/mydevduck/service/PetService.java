package com.mydevduck.service;

import com.mydevduck.dto.response.PetDTO;
import com.mydevduck.exception.InvalidRequestException;
import com.mydevduck.model.Pet;
import com.mydevduck.model.User;
import com.mydevduck.repository.PetRepository;
import com.mydevduck.repository.UserRepository;
import com.mydevduck.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public PetDTO createPet(String token, String name) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidRequestException("Invalid token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidRequestException("User not found."));

        long petCount = petRepository.countByUser(user);
        if (petCount >= 5) {
            throw new InvalidRequestException("User already has 5 pets!");
        }
        if (petRepository.existsByUserAndName(user, name)) {
            throw new InvalidRequestException("User already has a pet with this name!");
        }

        Pet pet = new Pet();
        pet.setUser(user);
        pet.setName(name);

        Pet savedPet = petRepository.save(pet);

        return new PetDTO(
            savedPet.getId(),
            savedPet.getName(),
            savedPet.getHealth(),
            savedPet.getHappiness(),
            savedPet.getHunger(),
            savedPet.getLevel(),
            savedPet.getXp(),
            savedPet.getLastFedAt(),
            savedPet.getLastPlayedAt(),
            savedPet.getCreatedAt(),
            savedPet.getUpdatedAt()
        );
    }

    public PetDTO getPetById(String token, UUID id) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidRequestException("Invalid token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidRequestException("User not found."));

        Pet pet = petRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new InvalidRequestException("Pet not found or you don't own this pet."));

        return new PetDTO(
                pet.getId(),
                pet.getName(),
                pet.getHealth(),
                pet.getHappiness(),
                pet.getHunger(),
                pet.getLevel(),
                pet.getXp(),
                pet.getLastFedAt(),
                pet.getLastPlayedAt(),
                pet.getCreatedAt(),
                pet.getUpdatedAt()
        );
    }
}
