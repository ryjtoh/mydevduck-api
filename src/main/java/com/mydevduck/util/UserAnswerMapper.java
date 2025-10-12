package com.mydevduck.util;

import com.mydevduck.dto.request.SubmitAnswerRequest;
import com.mydevduck.dto.response.UserAnswerDTO;
import com.mydevduck.model.MCQQuestion;
import com.mydevduck.model.UserAnswer;
import com.mydevduck.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAnswerMapper {

    private final ModelMapper modelMapper;

    // SubmitAnswerRequest -> Entity
    public UserAnswer toEntity(SubmitAnswerRequest request, User user, MCQQuestion question) {
        UserAnswer answer = new UserAnswer();
        answer.setUser(user);
        answer.setQuestion(question);
        answer.setSelectedAnswer(request.getSelectedAnswer());
        // isCorrect is automatically set by @PrePersist in UserAnswer entity
        return answer;
    }

    // Entity -> DTO (includes questionId and pointsEarned)
    public UserAnswerDTO toDTO(UserAnswer answer) {
        // ModelMapper uses custom mapping from ModelMapperConfig
        // It automatically extracts questionId and calculates pointsEarned
        return modelMapper.map(answer, UserAnswerDTO.class);
    }

}
