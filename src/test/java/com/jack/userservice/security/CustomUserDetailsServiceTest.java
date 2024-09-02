package com.jack.userservice.security;

import com.jack.userservice.entity.Users;
import com.jack.userservice.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Users sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new Users();
        sampleUser.setId(1L);
        sampleUser.setName("Jack Lee");
        sampleUser.setEmail("jacklee@example.com");
        sampleUser.setPassword("encodedPassword");
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        when(usersRepository.findByEmail(sampleUser.getEmail())).thenReturn(Optional.of(sampleUser));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(sampleUser.getEmail());

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(sampleUser.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(sampleUser.getPassword());
        assertThat(userDetails.getAuthorities()).isEmpty();  // Since we used Collections.emptyList() for authorities

        // Verify that the repository method was called exactly once
        verify(usersRepository, times(1)).findByEmail(sampleUser.getEmail());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        String nonExistentEmail = "jacklee@example.com";
        when(usersRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(nonExistentEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: " + nonExistentEmail);

        // Verify that the repository method was called exactly once
        verify(usersRepository, times(1)).findByEmail(nonExistentEmail);
    }
}
