package com.mydevduck.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePetRequest {

    @NotBlank(message = "Pet name is required")
    @Size(max = 50, message = "Pet name must not exceed 50 characters")
    private String name;

}
