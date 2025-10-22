package com.mydevduck.service;

import com.mydevduck.dto.response.PetDTO;
import com.mydevduck.dto.response.PetStatsDTO;
import com.mydevduck.exception.InvalidRequestException;
import com.mydevduck.model.Pet;
import com.mydevduck.model.User;
import com.mydevduck.repository.PetRepository;
import com.mydevduck.repository.UserRepository;
import com.mydevduck.security.JwtTokenProvider;
import com.mydevduck.util.PetMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PetMapper petMapper;

    @Transactional
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

        return petMapper.toDTO(savedPet);
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

        return petMapper.toDTO(pet);
    }

    @Transactional
    public PetDTO updatePet(String token, UUID id, String newName) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidRequestException("Invalid token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidRequestException("User not found."));

        Pet pet = petRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new InvalidRequestException("Pet not found or you don't own this pet."));

        // Check if new name is unique for this user (excluding current pet)
        if (!pet.getName().equals(newName) && petRepository.existsByUserAndName(user, newName)) {
            throw new InvalidRequestException("You already have a pet named " + newName);
        }

        pet.setName(newName);
        Pet updatedPet = petRepository.save(pet);

        return petMapper.toDTO(updatedPet);
    }

    @Transactional
    public void deletePet(String token, UUID id) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidRequestException("Invalid token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidRequestException("User not found."));

        Pet pet = petRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new InvalidRequestException("Pet not found or you don't own this pet."));

        petRepository.delete(pet);
    }

    @Transactional
    public PetDTO feedPet(String token, UUID id) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidRequestException("Invalid token.");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(token);
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User does not exist"));


        Pet pet = petRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new InvalidRequestException("User does not own pet."));

        if (pet.getHunger() > 90) {
            throw new InvalidRequestException("Pet hunger is above 90.");
        }

        pet.setHunger(Math.min(pet.getHunger() + 20, 100));
        pet.setXp(pet.getXp() + 5);
        pet.setUpdatedAt(LocalDateTime.now());
        pet.setLastFedAt(LocalDateTime.now());

        Pet updatedPet = petRepository.save(pet);

        return petMapper.toDTO(updatedPet);
    }

    @Transactional
    public PetDTO playWithPet(String token, UUID id) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidRequestException("Invalid token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("User with this email doesn't exist"));

        Pet pet = petRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new InvalidRequestException("User does not own this pet."));

        if (pet.getHappiness() > 90) {
            throw new InvalidRequestException("Pet has a happiness of 90.");
        }

        pet.setHappiness(Math.min(100, pet.getHappiness() + 15));
        pet.setXp(pet.getXp() + 3);
        pet.setLastPlayedAt(LocalDateTime.now());
        pet.setUpdatedAt(LocalDateTime.now());

        Pet updatedPet = petRepository.save(pet);

        return petMapper.toDTO(updatedPet);
    }

    public PetStatsDTO getStats(String token, UUID id) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidRequestException("Invalid token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("User with this email doesn't exist"));

        Pet pet = petRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new InvalidRequestException("User does not own this pet."));

        int calculatedLevel = pet.getXp() / 100;

        return PetStatsDTO.fromPet(
                pet.getHealth(),
                pet.getHappiness(),
                pet.getHunger(),
                calculatedLevel,
                pet.getXp(),
                pet.getLastFedAt(),
                pet.getLastPlayedAt(),
                pet.getCreatedAt()
        );

    }
}
