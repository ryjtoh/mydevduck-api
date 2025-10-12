package com.mydevduck.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayWithPetRequest {

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 120, message = "Duration must not exceed 120 minutes")
    private Integer duration = 15; // in minutes

}
