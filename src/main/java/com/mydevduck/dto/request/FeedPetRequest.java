package com.mydevduck.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedPetRequest {

    @Min(value = 1, message = "Amount must be at least 1")
    @Max(value = 100, message = "Amount must not exceed 100")
    private Integer amount = 20;

}
