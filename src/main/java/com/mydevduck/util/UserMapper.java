package com.mydevduck.util;

import com.mydevduck.dto.request.RegisterRequest;
import com.mydevduck.dto.response.UserDTO;
import com.mydevduck.model.User;
import com.mydevduck.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // RegisterRequest -> Entity (with password hashing)
    public User toEntity(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setGithubUsername(request.getGithubUsername());
        user.setRole(UserRole.USER); // Default role
        return user;
    }

    // Entity -> DTO (exclude password)
    public UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

}
