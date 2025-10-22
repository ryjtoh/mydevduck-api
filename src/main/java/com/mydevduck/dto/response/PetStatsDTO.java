package com.mydevduck.dto.response;

import com.mydevduck.model.PetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private PetStatus status;
    private Boolean needsAttention;

    // Time-based fields
    private Long hoursSinceLastFed;
    private Long hoursSinceLastPlayed;
    private Long ageInHours;
    private LocalDateTime lastFedAt;
    private LocalDateTime lastPlayedAt;
    private LocalDateTime createdAt;

    public static PetStatsDTO fromPet(Integer health, Integer happiness, Integer hunger, Integer level, Integer xp,
                                       LocalDateTime lastFedAt, LocalDateTime lastPlayedAt, LocalDateTime createdAt) {
        int nextLevelXp = (level + 1) * 100;
        int xpToNextLevel = nextLevelXp - xp;

        PetStatus status = determineStatus(health, happiness, hunger);
        boolean needsAttention = health < 30 || happiness < 30 || hunger < 30;

        LocalDateTime now = LocalDateTime.now();

        // Calculate hours since last interactions
        Long hoursSinceLastFed = lastFedAt != null
            ? java.time.Duration.between(lastFedAt, now).toHours()
            : null;

        Long hoursSinceLastPlayed = lastPlayedAt != null
            ? java.time.Duration.between(lastPlayedAt, now).toHours()
            : null;

        Long ageInHours = createdAt != null
            ? java.time.Duration.between(createdAt, now).toHours()
            : 0L;

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
                .hoursSinceLastFed(hoursSinceLastFed)
                .hoursSinceLastPlayed(hoursSinceLastPlayed)
                .ageInHours(ageInHours)
                .lastFedAt(lastFedAt)
                .lastPlayedAt(lastPlayedAt)
                .createdAt(createdAt)
                .build();
    }

    private static PetStatus determineStatus(Integer health, Integer happiness, Integer hunger) {
        // DYING takes priority - critically low stats
        if (hunger < 20 || happiness < 20) {
            return PetStatus.DYING;
        }

        // SAD - low happiness but not critical
        if (happiness < 50) {
            return PetStatus.SAD;
        }

        // HUNGRY - low hunger but not critical
        if (hunger < 50) {
            return PetStatus.HUNGRY;
        }

        // HEALTHY - all stats are good
        return PetStatus.HEALTHY;
    }

}
