package com.jack.userservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String name;
    private String email;
    private String password; // Optional during updates
}
