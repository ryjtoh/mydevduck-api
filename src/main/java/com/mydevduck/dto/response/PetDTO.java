package com.mydevduck.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetDTO {

    private UUID id;
    private String name;
    private Integer health;
    private Integer happiness;
    private Integer hunger;
    private Integer level;
    private Integer xp;
    private LocalDateTime lastFedAt;
    private LocalDateTime lastPlayedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
