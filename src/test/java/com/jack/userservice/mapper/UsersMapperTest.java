package com.jack.userservice.mapper;

import com.jack.userservice.dto.UsersDTO;
import com.jack.userservice.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsersMapperTest {

    private UsersMapper usersMapper;

    @BeforeEach
    void setUp() {
        usersMapper = new UsersMapper();
    }

    @Test
    void toDto_shouldMapUsersToUsersDTO() {
        // Arrange
        Users users = Users.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe@example.com")
                .password("password123")
                .build();

        // Act
        UsersDTO usersDTO = usersMapper.toDto(users);

        // Assert
        assertThat(usersDTO).isNotNull();
        assertThat(usersDTO.getId()).isEqualTo(users.getId());
        assertThat(usersDTO.getName()).isEqualTo(users.getName());
        assertThat(usersDTO.getEmail()).isEqualTo(users.getEmail());
        // Password should not be mapped to the DTO
        assertThat(usersDTO.getPassword()).isNull();
    }

    @Test
    void toEntity_shouldMapUsersDTOToUsers() {
        // Arrange
        UsersDTO usersDTO = UsersDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe@example.com")
                .password("password123")
                .build();

        // Act
        Users users = usersMapper.toEntity(usersDTO);

        // Assert
        assertThat(users).isNotNull();
        assertThat(users.getId()).isNull(); // ID should not be set when converting from DTO to Entity
        assertThat(users.getName()).isEqualTo(usersDTO.getName());
        assertThat(users.getEmail()).isEqualTo(usersDTO.getEmail());
        assertThat(users.getPassword()).isEqualTo(usersDTO.getPassword());
    }
}
