package com.mydevduck.service;

import com.mydevduck.dto.response.PetDTO;
import com.mydevduck.model.User;
import com.mydevduck.repository.PetRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    public PetDTO createPet(String email, String name) {

    }
}
