package com.jack.userservice.controller;

import com.jack.userservice.dto.UsersDTO;
import com.jack.userservice.entity.Users;
import com.jack.userservice.mapper.UsersMapper;
import com.jack.userservice.security.JwtTokenProvider;
import com.jack.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UsersMapper usersMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserController userController;

    private Users sampleUser;
    private UsersDTO sampleUserDTO;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        sampleUser = Users.builder()
                .id(1L)
                .name("Jack Lee")
                .email("jacklee@example.com")
                .password("encodedPassword")
                .build();

        sampleUserDTO = UsersDTO.builder()
                .id(1L)
                .name("Jack Lee")
                .email("jacklee@example.com")
                .build();

        authentication = new UsernamePasswordAuthenticationToken(sampleUser.getEmail(), sampleUser.getPassword());
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        when(usersMapper.toEntity(any(UsersDTO.class))).thenReturn(sampleUser);
        when(userService.registerUser(any(Users.class))).thenReturn(sampleUser);
        when(usersMapper.toDto(any(Users.class))).thenReturn(sampleUserDTO);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Jack Lee\", \"email\": \"jacklee@example.com\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Jack Lee"))
                .andExpect(jsonPath("$.email").value("jacklee@example.com"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        when(usersMapper.toEntity(any(UsersDTO.class))).thenReturn(sampleUser);
        when(userService.updateUser(eq(1L), any(Users.class))).thenReturn(Optional.of(sampleUser));
        when(usersMapper.toDto(any(Users.class))).thenReturn(sampleUserDTO);

        String jsonRequest = "{\"name\": \"Jack Lee\", \"email\": \"jacklee@example.com\", \"password\": \"newpassword\"}";

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Jack Lee"))
                .andExpect(jsonPath("$.email").value("jacklee@example.com"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(sampleUser));
        when(usersMapper.toDto(any(Users.class))).thenReturn(sampleUserDTO);

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Jack Lee"))
                .andExpect(jsonPath("$.email").value("jacklee@example.com"));
    }

    @Test
    void testLogin_Success() throws Exception {
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("mockedToken");

        String jsonRequest = "{\"email\": \"jacklee@example.com\", \"password\": \"password\"}";

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockedToken"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        String jsonRequest = "{\"email\": \"jacklee@example.com\", \"password\": \"wrongpassword\"}";

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogout_Success() throws Exception {
        mockMvc.perform(get("/api/users/logout"))
                .andExpect(status().isOk());
    }
}
