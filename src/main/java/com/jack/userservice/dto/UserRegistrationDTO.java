package com.jack.userservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    private String name;
    private String email;
    private String password; // Required during registration
}
