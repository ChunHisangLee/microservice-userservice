package com.jack.userservice.mapper;

import com.jack.userservice.dto.UsersDTO;
import com.jack.userservice.dto.UserRegistrationDTO;
import com.jack.userservice.dto.UserUpdateDTO;
import com.jack.userservice.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class UsersMapper {

    // Map Users entity to UsersDTO (for general use without password)
    public UsersDTO toDto(Users users) {
        return UsersDTO.builder()
                .id(users.getId())
                .name(users.getName())
                .email(users.getEmail())
                .build();
    }

    // Map UserRegistrationDTO to Users entity (for registration)
    public Users toEntity(UserRegistrationDTO userRegistrationDTO) {
        return Users.builder()
                .name(userRegistrationDTO.getName())
                .email(userRegistrationDTO.getEmail())
                .password(userRegistrationDTO.getPassword()) // Password is set during registration
                .build();
    }

    // Map UserUpdateDTO to Users entity (for updates, handle optional password)
    public Users toEntity(UserUpdateDTO userUpdateDTO, Users existingUser) {
        existingUser.setName(userUpdateDTO.getName());
        existingUser.setEmail(userUpdateDTO.getEmail());
        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            existingUser.setPassword(userUpdateDTO.getPassword()); // Password is updated if provided
        }
        return existingUser;
    }
}
