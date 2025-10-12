package com.mydevduck.config;

import com.mydevduck.dto.response.*;
import com.mydevduck.model.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        // Custom mappings
        configureUserMapping(modelMapper);
        configurePetMapping(modelMapper);
        configureActivityMapping(modelMapper);
        configureMCQQuestionMapping(modelMapper);
        configureUserAnswerMapping(modelMapper);

        return modelMapper;
    }

    private void configureUserMapping(ModelMapper modelMapper) {
        // User to UserDTO - password is already excluded by field name mismatch
        modelMapper.createTypeMap(User.class, UserDTO.class);
    }

    private void configurePetMapping(ModelMapper modelMapper) {
        // Pet to PetDTO - user relationship is already excluded by @JsonIgnore
        modelMapper.createTypeMap(Pet.class, PetDTO.class);
    }

    private void configureActivityMapping(ModelMapper modelMapper) {
        // Activity to ActivityDTO - user relationship is already excluded
        modelMapper.createTypeMap(Activity.class, ActivityDTO.class);
    }

    private void configureMCQQuestionMapping(ModelMapper modelMapper) {
        // MCQQuestion to MCQQuestionDTO - correctAnswer is excluded by field name mismatch
        modelMapper.createTypeMap(MCQQuestion.class, MCQQuestionDTO.class);
    }

    private void configureUserAnswerMapping(ModelMapper modelMapper) {
        modelMapper.createTypeMap(UserAnswer.class, UserAnswerDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getQuestion().getId(), UserAnswerDTO::setQuestionId);
                    mapper.map(src -> src.getQuestion().getPoints(), UserAnswerDTO::setPointsEarned);
                });
    }

}
