package com.jack.userservice.controller;

import com.jack.userservice.dto.UserRegistrationDTO;
import com.jack.userservice.dto.UsersDTO;
import com.jack.userservice.entity.Users;
import com.jack.userservice.exception.CustomErrorException;
import com.jack.userservice.mapper.UsersMapper;
import com.jack.userservice.service.UserService;
import com.jack.userservice.service.AuthServiceClient;  // New: To communicate with auth-service
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.jack.userservice.constants.ErrorMessages.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UsersMapper usersMapper;
    private final AuthServiceClient authServiceClient;  // New: To communicate with auth-service

    public UserController(UserService userService, UsersMapper usersMapper, AuthServiceClient authServiceClient) {
        this.userService = userService;
        this.usersMapper = usersMapper;
        this.authServiceClient = authServiceClient;
    }

    @PostMapping("/register")
    public ResponseEntity<UsersDTO> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        logger.info("Registering new user with email: {}", userRegistrationDTO.getEmail());

        // Register the user (save in DB)
        Users users = usersMapper.toEntity(userRegistrationDTO);
        Users createdUser = userService.registerUser(users);
        logger.info("User registered successfully with ID: {}", createdUser.getId());

        // Call auth-service to generate JWT token
        String token = authServiceClient.generateToken(createdUser.getEmail());
        logger.info("JWT Token generated for registered user: {}", createdUser.getEmail());

        UsersDTO responseDto = usersMapper.toDto(createdUser);
        return ResponseEntity.ok(responseDto);  // Return user details, optionally return the token in response if needed
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsersDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        logger.info("Updating user with ID: {}", id);
        Users users = usersMapper.toEntity(userRegistrationDTO);
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

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDetails> getUserByEmail(@PathVariable String email) {
        logger.info("Fetching user with email: {}", email);
        UserDetails user = userService.loadUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}
