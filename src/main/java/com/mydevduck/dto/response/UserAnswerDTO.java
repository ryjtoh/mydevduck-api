package com.mydevduck.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerDTO {

    private UUID id;
    private UUID questionId;
    private Integer selectedAnswer;
    private Boolean isCorrect;
    private Integer pointsEarned;
    private LocalDateTime answeredAt;

}
