package com.jack.userservice.service.impl;

import com.jack.userservice.entity.Users;
import com.jack.userservice.exception.CustomErrorException;
import com.jack.userservice.message.WalletCreationMessage;
import com.jack.userservice.repository.UsersRepository;
import com.jack.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.jack.userservice.constants.ErrorMessages.*;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    public UserServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder, RabbitTemplate rabbitTemplate) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Users registerUser(Users users) {
        logger.info("Attempting to register user with email: {}", users.getEmail());
        if (usersRepository.findByEmail(users.getEmail()).isPresent()) {
            logger.error("Email {} is already registered.", users.getEmail());
            throw new CustomErrorException(
                    HttpStatus.CONFLICT.value(),
                    CONFLICT_STATUS,
                    EMAIL_ALREADY_REGISTERED,
                    POST_USER_API_PATH
            );
        }

        // Encode the user's password
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        logger.debug("Password encoded for user with email: {}", users.getEmail());

        // Save the user and cascade save the wallet
        Users savedUser = usersRepository.save(users);
        logger.info("User with email: {} registered successfully with ID: {}", savedUser.getEmail(), savedUser.getId());

        // Send a message to RabbitMQ for wallet creation
        WalletCreationMessage walletMessage = new WalletCreationMessage(savedUser.getId(), 1000.0);

        try {
            rabbitTemplate.convertAndSend("walletExchange", "walletRoutingKey", walletMessage);
            logger.info("Wallet creation message sent for user ID: {}", savedUser.getId());
        } catch (AmqpException e) {
            logger.error("Failed to send message to RabbitMQ: {}", e.getMessage());
            throw e;
        }

        return savedUser;
    }

    @Override
    public Optional<Users> updateUser(Long id, Users users) {
        logger.info("Attempting to update user with ID: {}", id);
        Users existingUser = usersRepository.findById(id).orElseThrow(() -> {
            logger.error("User with ID: {} not found for update.", id);
            return new CustomErrorException(
                    HttpStatus.NOT_FOUND.value(),
                    NOT_FOUND_STATUS,
                    USER_NOT_FOUND,
                    PUT_USER_API_PATH + id
            );
        });

        if (usersRepository.findByEmail(users.getEmail()).filter(user -> !user.getId().equals(id)).isPresent()) {
            logger.error("Email {} is already registered by another user.", users.getEmail());
            throw new CustomErrorException(
                    HttpStatus.CONFLICT.value(),
                    CONFLICT_STATUS,
                    EMAIL_ALREADY_REGISTERED_BY_ANOTHER_USER,
                    PUT_USER_API_PATH + id
            );
        }

        existingUser.setName(users.getName());
        existingUser.setEmail(users.getEmail());
        logger.debug("User details updated for user with ID: {}", id);

        if (users.getPassword() != null && !users.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(users.getPassword()));
            logger.debug("Password updated for user with ID: {}", id);
        }

        Users updatedUser = usersRepository.save(existingUser);
        logger.info("User with ID: {} updated successfully.", id);
        return Optional.of(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Attempting to delete user with ID: {}", id);
        Users user = usersRepository.findById(id).orElseThrow(() -> {
            logger.error("User with ID: {} not found for deletion.", id);
            return new CustomErrorException(
                    HttpStatus.NOT_FOUND.value(),
                    NOT_FOUND_STATUS,
                    USER_NOT_FOUND,
                    DELETE_USER_API_PATH + id
            );
        });
        usersRepository.delete(user);
        logger.info("User with ID: {} deleted successfully.", id);
    }

    @Override
    public Users login(String email, String password) {
        logger.info("User login attempt with email: {}", email);
        Users user = usersRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("Invalid email or password for email: {}", email);
            return new CustomErrorException(
                    HttpStatus.UNAUTHORIZED.value(),
                    UNAUTHORIZED_STATUS,
                    INVALID_EMAIL_OR_PASSWORD,
                    POST_LOGIN_API_PATH
            );
        });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.error("Invalid password for email: {}", email);
            throw new CustomErrorException(
                    HttpStatus.UNAUTHORIZED.value(),
                    UNAUTHORIZED_STATUS,
                    INVALID_EMAIL_OR_PASSWORD,
                    POST_LOGIN_API_PATH
            );
        }

        logger.info("User with email: {} logged in successfully.", email);
        return user;
    }

    @Override
    public Optional<Users> getUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);
        return Optional.ofNullable(usersRepository.findById(id).orElseThrow(() -> {
            logger.error("User with ID: {} not found.", id);
            return new CustomErrorException(
                    HttpStatus.NOT_FOUND.value(),
                    NOT_FOUND_STATUS,
                    USER_NOT_FOUND,
                    GET_USER_API_PATH + id
            );
        }));
    }

    @Override
    public Optional<Users> findByEmail(String email) {
        logger.info("Fetching user by email: {}", email);
        return usersRepository.findByEmail(email);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        logger.debug("Verifying password for authentication.");
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
