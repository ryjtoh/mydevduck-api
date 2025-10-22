package com.mydevduck.controller;

import com.mydevduck.dto.request.CreatePetRequest;
import com.mydevduck.dto.response.PetDTO;
import com.mydevduck.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping("/create")
    public PetDTO create(@RequestBody @Valid CreatePetRequest request, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return petService.createPet(token, request.getName());
    }
}
