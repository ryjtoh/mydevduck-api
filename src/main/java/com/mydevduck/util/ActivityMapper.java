package com.mydevduck.util;

import com.mydevduck.dto.request.CreateActivityRequest;
import com.mydevduck.dto.response.ActivityDTO;
import com.mydevduck.model.Activity;
import com.mydevduck.model.ActivityType;
import com.mydevduck.model.User;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    // DTO -> Entity
    public Activity toEntity(CreateActivityRequest request, User user) {
        Activity activity = new Activity();
        activity.setUser(user);
        activity.setType(request.getType());
        activity.setDescription(request.getDescription());
        activity.setMetadata(request.getMetadata());
        activity.setPoints(calculatePoints(request.getType()));
        return activity;
    }

    // Entity -> DTO
    public ActivityDTO toDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setType(activity.getType());
        dto.setDescription(activity.getDescription());
        dto.setPoints(activity.getPoints());
        dto.setMetadata(activity.getMetadata());
        dto.setCreatedAt(activity.getCreatedAt());
        return dto;
    }


    private Integer calculatePoints(ActivityType type) {
        return switch(type) {
            case COMMIT, MANUAL, ISSUE -> 10;
            case CODE_REVIEW -> 15;
            case PULL_REQUEST -> 40;
            case LEETCODE_EASY -> 15;
            case LEETCODE_MEDIUM -> 30;
            case LEETCODE_HARD -> 50;
            case MCQ_COMPLETED -> 20;
            case DAILY_STREAK -> 5;
            case WEEKLY_STREAK -> 25;
            case MONTHLY_STREAK -> 100;
        };
    }
}
