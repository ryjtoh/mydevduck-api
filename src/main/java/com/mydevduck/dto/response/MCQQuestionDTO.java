package com.mydevduck.dto.response;

import com.mydevduck.model.Category;
import com.mydevduck.model.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MCQQuestionDTO {

    private UUID id;
    private String question;
    private List<String> options;
    private Difficulty difficulty;
    private Category category;
    private Integer points;
    // Note: correctAnswer is NOT included for security

}
