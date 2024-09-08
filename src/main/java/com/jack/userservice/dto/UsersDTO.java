package com.jack.userservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {
    private Long id;
    private String name;
    private String email;
}
