package com.mydevduck.util;

import com.mydevduck.dto.response.MCQQuestionDTO;
import com.mydevduck.model.MCQQuestion;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MCQQuestionMapper {

    private final ModelMapper modelMapper;

    // Entity -> DTO (exclude correctAnswer for security)
    public MCQQuestionDTO toDTO(MCQQuestion question) {
        // ModelMapper automatically excludes correctAnswer because it's not in MCQQuestionDTO
        return modelMapper.map(question, MCQQuestionDTO.class);
    }

    // Note: We don't create a toEntity method because questions are typically
    // seeded/created by admins, not through DTOs in regular flow

}
