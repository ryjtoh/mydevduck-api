package com.mydevduck.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {

    @NotNull(message = "Question ID is required")
    private UUID questionId;

    @NotNull(message = "Selected answer is required")
    @Min(value = 0, message = "Selected answer must be between 0 and 3")
    @Max(value = 3, message = "Selected answer must be between 0 and 3")
    private Integer selectedAnswer;

}
