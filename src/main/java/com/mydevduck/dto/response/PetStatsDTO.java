package com.mydevduck.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetStatsDTO {

    private Integer totalXp;
    private Integer currentLevel;
    private Integer nextLevelXp;
    private Integer xpToNextLevel;
    private Double healthPercentage;
    private Double happinessPercentage;
    private Double hungerPercentage;
    private String status; // e.g., "Healthy", "Hungry", "Sad", "Critical"
    private Boolean needsAttention;

    public static PetStatsDTO fromPet(Integer health, Integer happiness, Integer hunger, Integer level, Integer xp) {
        int nextLevelXp = level * 100;
        int xpToNextLevel = nextLevelXp - xp;

        String status = determineStatus(health, happiness, hunger);
        boolean needsAttention = health < 30 || happiness < 30 || hunger > 70;

        return PetStatsDTO.builder()
                .totalXp(xp)
                .currentLevel(level)
                .nextLevelXp(nextLevelXp)
                .xpToNextLevel(xpToNextLevel)
                .healthPercentage(health.doubleValue())
                .happinessPercentage(happiness.doubleValue())
                .hungerPercentage(hunger.doubleValue())
                .status(status)
                .needsAttention(needsAttention)
                .build();
    }

    private static String determineStatus(Integer health, Integer happiness, Integer hunger) {
        if (health < 20 || happiness < 20 || hunger > 90) {
            return "Critical";
        } else if (health < 40 || happiness < 40 || hunger > 70) {
            return "Needs Care";
        } else if (hunger > 60) {
            return "Hungry";
        } else if (happiness < 60) {
            return "Sad";
        } else {
            return "Healthy";
        }
    }

}
