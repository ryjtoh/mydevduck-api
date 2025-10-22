package com.mydevduck.controller;

import com.mydevduck.dto.request.CreatePetRequest;
import com.mydevduck.dto.request.UpdatePetRequest;
import com.mydevduck.dto.response.PetDTO;
import com.mydevduck.dto.response.PetStatsDTO;
import com.mydevduck.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping("/create")
    public ResponseEntity<PetDTO> create(@RequestBody @Valid CreatePetRequest request, @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        PetDTO pet = petService.createPet(token, request.getName());
        return ResponseEntity.status(201).body(pet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> get(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        PetDTO pet = petService.getPetById(token, id);
        return ResponseEntity.ok(pet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdatePetRequest request, @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        PetDTO pet = petService.updatePet(token, id, request.getName());
        return ResponseEntity.ok(pet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        petService.deletePet(token, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/feed")
    public ResponseEntity<PetDTO> feed(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        PetDTO pet = petService.feedPet(token, id);
        return ResponseEntity.ok(pet);
    }

    @PostMapping("/{id}/play")
    public ResponseEntity<PetDTO> play(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        PetDTO pet = petService.playWithPet(token, id);
        return ResponseEntity.ok(pet);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<PetStatsDTO> stats(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        PetStatsDTO stats = petService.getStats(token, id);
        return ResponseEntity.ok(stats);
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header format");
        }
        return authHeader.substring(7);
    }
}
