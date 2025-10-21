package com.mydevduck.controller;

import com.mydevduck.dto.request.CreatePetRequest;
import com.mydevduck.dto.response.PetDTO;
import com.mydevduck.model.Pet;
import com.mydevduck.repository.PetRepository;
import com.mydevduck.repository.UserRepository;
import com.mydevduck.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping("/create")
    public PetDTO create(@RequestBody CreatePetRequest request) {
        return petService.createPet(request.getToken(), request.getName());
    }
}
