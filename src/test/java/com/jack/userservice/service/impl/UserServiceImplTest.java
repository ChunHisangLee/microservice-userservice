package com.jack.userservice.service.impl;

import com.jack.userservice.config.RabbitMQConfig;
import com.jack.userservice.entity.Users;
import com.jack.userservice.exception.CustomErrorException;
import com.jack.userservice.message.WalletCreationMessage;
import com.jack.userservice.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    private Users user;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .id(1L)
                .email("jacklee@example.com")
                .name("Jack Lee")
                .password("encodedPassword")
                .build();
    }

    @Test
    void registerUser_Success() {
        // Arrange
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        // Act
        Users savedUser = userService.registerUser(user);

        // Assert
        assertNotNull(savedUser);
        assertEquals(user.getId(), savedUser.getId());
        verify(usersRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(usersRepository, times(1)).save(any(Users.class));

        // Verify RabbitMQ interaction with the correct queue
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.USER_CREATED_QUEUE), // Correct queue from RabbitMQConfig
                any(WalletCreationMessage.class)
        );
    }

    @Test
    void registerUser_EmailAlreadyRegistered() {
        // Arrange
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act & Assert
        CustomErrorException exception = assertThrows(CustomErrorException.class, () -> userService.registerUser(user));
        assertEquals(HttpStatus.CONFLICT.value(), exception.getStatusCode());

        // Verify that no user was saved
        verify(usersRepository, times(1)).findByEmail(anyString());
        verify(usersRepository, never()).save(any(Users.class));

        // Verify that RabbitMQ was not called
        verify(rabbitTemplate, never()).convertAndSend(
                any(String.class),  // queue
                any(WalletCreationMessage.class)   // message
        );
    }

    @Test
    void updateUser_Success() {
        // Arrange
        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        // Act
        Optional<Users> updatedUser = userService.updateUser(1L, user);

        // Assert
        assertTrue(updatedUser.isPresent());
        assertEquals(user.getId(), updatedUser.get().getId());
        verify(usersRepository, times(1)).findById(anyLong());
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    void updateUser_UserNotFound() {
        // Arrange
        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        CustomErrorException exception = assertThrows(CustomErrorException.class, () -> userService.updateUser(1L, user));
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
        verify(usersRepository, times(1)).findById(anyLong());
        verify(usersRepository, never()).save(any(Users.class));
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doNothing().when(usersRepository).delete(any(Users.class));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(usersRepository, times(1)).findById(anyLong());
        verify(usersRepository, times(1)).delete(any(Users.class));
    }

    @Test
    void deleteUser_UserNotFound() {
        // Arrange
        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        CustomErrorException exception = assertThrows(CustomErrorException.class, () -> userService.deleteUser(1L));
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
        verify(usersRepository, times(1)).findById(anyLong());
        verify(usersRepository, never()).delete(any(Users.class));
    }

    @Test
    void login_Success() {
        // Arrange
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        Users loggedInUser = userService.login("jacklee@example.com", "password");

        // Assert
        assertNotNull(loggedInUser);
        assertEquals(user.getId(), loggedInUser.getId());
        verify(usersRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    void login_InvalidPassword() {
        // Arrange
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        CustomErrorException exception = assertThrows(CustomErrorException.class, () -> userService.login("jacklee@example.com", "wrongpassword"));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.getStatusCode());
        verify(usersRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Act
        Optional<Users> foundUser = userService.getUserById(1L);

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
        verify(usersRepository, times(1)).findById(anyLong());
    }

    @Test
    void getUserById_UserNotFound() {
        // Arrange
        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        CustomErrorException exception = assertThrows(CustomErrorException.class, () -> userService.getUserById(1L));
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
        verify(usersRepository, times(1)).findById(anyLong());
    }

    @Test
    void findByEmail_Success() {
        // Arrange
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act
        Optional<Users> foundUser = userService.findByEmail("jacklee@example.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
        verify(usersRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void verifyPassword_Success() {
        // Arrange
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        boolean isMatch = userService.verifyPassword("rawPassword", "encodedPassword");

        // Assert
        assertTrue(isMatch);
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }
}
