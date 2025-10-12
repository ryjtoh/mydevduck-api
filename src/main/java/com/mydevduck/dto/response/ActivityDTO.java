package com.mydevduck.dto.response;

import com.mydevduck.model.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {

    private UUID id;
    private ActivityType type;
    private String description;
    private Integer points;
    private String metadata;
    private LocalDateTime createdAt;

}
