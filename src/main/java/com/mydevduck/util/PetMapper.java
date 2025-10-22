package com.mydevduck.util;

import com.mydevduck.dto.request.CreatePetRequest;
import com.mydevduck.dto.response.PetDTO;
import com.mydevduck.model.Pet;
import com.mydevduck.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PetMapper {

    // CreatePetRequest -> Entity
    public Pet toEntity(CreatePetRequest request, User user) {
        Pet pet = new Pet();
        pet.setUser(user);
        pet.setName(request.getName());
        // Default values are set by field initialization in Pet entity
        // health = 100, happiness = 100, hunger = 50, level = 1, xp = 0
        return pet;
    }

    // Entity -> DTO
    public PetDTO toDTO(Pet pet) {
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

    // List<Entity> -> List<DTO>
    public List<PetDTO> toDTOList(List<Pet> pets) {
        return pets.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

}
