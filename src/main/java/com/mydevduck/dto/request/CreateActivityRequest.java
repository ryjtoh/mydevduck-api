package com.mydevduck.dto.request;

import com.mydevduck.model.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityRequest {

    @NotNull(message = "Activity type is required")
    private ActivityType type;

    @NotBlank(message = "Description is required")
    private String description;

    private String metadata;

}
