package com.jack.userservice.controller;

import com.jack.userservice.client.AuthServiceClient;
import com.jack.userservice.dto.*;
import com.jack.userservice.entity.Users;
import com.jack.userservice.exception.CustomErrorException;
import com.jack.userservice.mapper.UsersMapper;
import com.jack.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import static com.jack.userservice.constants.ErrorMessages.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UsersMapper usersMapper;
    private final AuthServiceClient authServiceClient;

    public UserController(UserService userService, UsersMapper usersMapper, AuthServiceClient authServiceClient) {
        this.userService = userService;
        this.usersMapper = usersMapper;
        this.authServiceClient = authServiceClient;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        UserResponseDTO userResponse = userService.register(userRegistrationDTO);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/verify-password")
    public ResponseEntity<Boolean> verifyPassword(@RequestBody AuthRequestDTO authRequestDTO) {
        boolean isPasswordValid = userService.verifyPassword(authRequestDTO.getEmail(), authRequestDTO.getPassword());
        return ResponseEntity.ok(isPasswordValid);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsersDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UsersDTO usersDTO) {
        logger.info("Updating user with ID: {}", id);
        Users users = usersMapper.toEntity(usersDTO);
        Users updatedUser = userService.updateUser(id, users)
                .orElseThrow(() -> {
                    logger.error("User with ID: {} not found for update.", id);
                    return new CustomErrorException(
                            HttpStatus.NOT_FOUND.value(),
                            NOT_FOUND_STATUS,
                            USER_NOT_FOUND,
                            GET_USER_API_PATH + id
                    );
                });

        logger.info("User with ID: {} updated successfully.", id);
        return ResponseEntity.ok(usersMapper.toDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        logger.info("User with ID: {} deleted successfully.", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersDTO> getUserById(@PathVariable Long id) {
        logger.info("Fetching user with ID: {}", id);
        Users user = userService.getUserById(id)
                .orElseThrow(() -> {
                    logger.error("User with ID: {} not found.", id);
                    return new CustomErrorException(
                            HttpStatus.NOT_FOUND.value(),
                            NOT_FOUND_STATUS,
                            USER_NOT_FOUND,
                            GET_USER_API_PATH + id
                    );
                });

        logger.info("User with ID: {} found.", id);
        return ResponseEntity.ok(usersMapper.toDto(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO loginRequest) {
        logger.info("User login attempt with email: {}", loginRequest.getEmail());

        if (!userService.verifyPassword(loginRequest.getEmail(), loginRequest.getPassword())) {
            logger.error("Invalid credentials for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // If credentials are valid, request JWT token from auth-service
        AuthResponseDTO authResponse = authServiceClient.login(loginRequest);
        logger.info("User with email: {} logged in successfully.", loginRequest.getEmail());
        // Return the JWT token received from auth-service
        return ResponseEntity.ok(authResponse);
    }


    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            logger.info("User with principal: {} logging out.", authentication.getName());
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok().build();
    }
}
