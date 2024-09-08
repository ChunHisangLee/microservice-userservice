package com.jack.userservice.mapper;

import com.jack.userservice.dto.UsersDTO;
import com.jack.userservice.dto.UserRegistrationDTO;
import com.jack.userservice.dto.UserUpdateDTO;
import com.jack.userservice.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class UsersMapper {

    // Map Users entity to UsersDTO (for general use without a password)
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

    // Map from UserUpdateDTO to User entity (for profile updates)
    public void updateUserFromDto(UserUpdateDTO userUpdateDTO, Users users) {
        users.setName(userUpdateDTO.getName());
        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            users.setPassword(userUpdateDTO.getPassword()); // Ensure password encoding in the service layer
        }
    }
}
