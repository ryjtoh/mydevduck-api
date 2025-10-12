package com.mydevduck.dto.response;

import com.mydevduck.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private UUID id;
    private String email;
    private String githubUsername;
    private UserRole role;
    private LocalDateTime createdAt;

}
