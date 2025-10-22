package com.mydevduck.scheduled;

import com.mydevduck.model.Pet;
import com.mydevduck.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetStatDecayTask {

    private final PetRepository petRepository;
    private static final int BATCH_SIZE = 100;

    /**
     * Runs every hour to decay pet statistics
     * - Decreases hunger by 10
     * - Decreases happiness by 5
     * - Decreases health if hunger or happiness are critically
     low
     * - Marks pets as dead if health reaches 0
     */
    @Scheduled(fixedRate = 3600000) // Every hour (3600000 ms)
    @Transactional
    public void decayPetStats() {
        log.info("Starting pet stat decay task...");

        int totalPetsProcessed = 0;
        int totalDeaths = 0;
        int pageNumber = 0;

        try {
            Page<Pet> petPage;

            do {
                // Load pets in batches of 100
                Pageable pageable = PageRequest.of(pageNumber,
                        BATCH_SIZE);
                petPage =
                        petRepository.findByIsDeadFalse(pageable);

                List<Pet> petsToUpdate = new ArrayList<>();

                for (Pet pet : petPage.getContent()) {
                    // Apply stat decay
                    pet.setHunger(Math.max(0, pet.getHunger() -
                            10));
                    pet.setHappiness(Math.max(0,
                            pet.getHappiness() - 5));

                    // Health decay based on low stats
                    if (pet.getHunger() < 20) {
                        pet.setHealth(Math.max(0, pet.getHealth()
                                - 5));
                    }
                    if (pet.getHappiness() < 20) {
                        pet.setHealth(Math.max(0, pet.getHealth()
                                - 3));
                    }

                    // Check for death
                    if (pet.getHealth() <= 0) {
                        pet.setDead(true);
                        totalDeaths++;
                        log.warn("Pet '{}' (ID: {}) has died",
                                pet.getName(), pet.getId());
                    }

                    petsToUpdate.add(pet);
                    totalPetsProcessed++;
                }

                // Batch update all pets in this chunk
                if (!petsToUpdate.isEmpty()) {
                    petRepository.saveAll(petsToUpdate);
                }

                pageNumber++;

            } while (petPage.hasNext());

            log.info("Pet stat decay task completed. Processed: {} pets, Deaths: {}",
            totalPetsProcessed, totalDeaths);

        } catch (Exception e) {
            log.error("Error during pet stat decay task", e);
        }
    }
}
