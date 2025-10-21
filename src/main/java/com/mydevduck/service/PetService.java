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

import java.util.Optional;

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

        if (!userRepository.existsByEmail(email)) {
            throw new InvalidRequestException("User with this email does not exist.");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new InvalidRequestException("User not found.");
        }

        User user = userOptional.get();

        Pet pet = new Pet();
        pet.setUser(user);
        pet.setName(name);

        return new PetDTO(pet.getId(), pet.getName(), pet.getHealth(), pet.getHappiness(), pet.getHunger(), pet.getLevel(), pet.getXp(), pet.getLastFedAt(), pet.getLastPlayedAt(), pet.getCreatedAt(), pet.getUpdatedAt());
    }
}
