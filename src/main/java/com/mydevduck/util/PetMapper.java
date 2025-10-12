package com.mydevduck.util;

import com.mydevduck.dto.request.CreatePetRequest;
import com.mydevduck.dto.response.PetDTO;
import com.mydevduck.model.Pet;
import com.mydevduck.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetMapper {

    private final ModelMapper modelMapper;

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
        return modelMapper.map(pet, PetDTO.class);
    }

}
