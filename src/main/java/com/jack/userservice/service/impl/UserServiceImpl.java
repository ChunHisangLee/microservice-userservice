package com.jack.userservice.service.impl;

import com.jack.userservice.config.RabbitMQConfig;
import com.jack.userservice.entity.Users;
import com.jack.userservice.exception.CustomErrorException;
import com.jack.userservice.message.WalletCreationMessage;
import com.jack.userservice.repository.UsersRepository;
import com.jack.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.jack.userservice.constants.ErrorMessages.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    public Users registerUser(Users users) {
        logger.info("Attempting to register user with email: {}", users.getEmail());
        validateUserEmail(users.getEmail());

        // Encode the user's password
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        logger.debug("Password encoded for user with email: {}", users.getEmail());

        // Save the user and cascade save the wallet
        Users savedUser = usersRepository.save(users);
        logger.info("User with email: {} registered successfully with ID: {}", savedUser.getEmail(), savedUser.getId());

        // Send a message to RabbitMQ for wallet creation
        double initialBalance = 1000.0;
        sendWalletCreationMessage(savedUser.getId(), initialBalance);

        return savedUser;
    }

    @Override
    public Optional<Users> updateUser(Long id, Users users) {
        logger.info("Attempting to update user with ID: {}", id);
        Users existingUser = findUserById(id);

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
        Users user = findUserById(id);
        usersRepository.delete(user);
        logger.info("User with ID: {} deleted successfully.", id);
    }

    @Override
    public Users login(String email, String password) {
        logger.info("User login attempt with email: {}", email);
        Users user = findUserByEmail(email);

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
        return Optional.of(findUserById(id));
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

    @Override
    public UserDetails loadUserByEmail(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
    }

    private void validateUserEmail(String email) {
        if (usersRepository.findByEmail(email).isPresent()) {
            logger.error("Email {} is already registered.", email);
            throw new CustomErrorException(
                    HttpStatus.CONFLICT.value(),
                    CONFLICT_STATUS,
                    EMAIL_ALREADY_REGISTERED,
                    POST_USER_API_PATH
            );
        }
    }

    private Users findUserById(Long id) {
        return usersRepository.findById(id).orElseThrow(() -> {
            logger.error("User with ID: {} not found.", id);
            return new CustomErrorException(
                    HttpStatus.NOT_FOUND.value(),
                    NOT_FOUND_STATUS,
                    USER_NOT_FOUND,
                    GET_USER_API_PATH + id
            );
        });
    }

    private Users findUserByEmail(String email) {
        return usersRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("Invalid email or password for email: {}", email);
            return new CustomErrorException(
                    HttpStatus.UNAUTHORIZED.value(),
                    UNAUTHORIZED_STATUS,
                    INVALID_EMAIL_OR_PASSWORD,
                    POST_LOGIN_API_PATH
            );
        });
    }

    private void sendWalletCreationMessage(Long userId, Double initialBalance) {
        WalletCreationMessage walletMessage = new WalletCreationMessage(userId, initialBalance);

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.USER_CREATED_QUEUE, walletMessage);
            logger.info("Wallet creation message sent for user ID: {}", userId);
        } catch (AmqpException e) {
            logger.error("Failed to send message to RabbitMQ: {}", e.getMessage());
            throw new CustomErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR_STATUS,
                    FAILED_WALLET_CREATION,
                    POST_USER_API_PATH
            );
        }
    }
}
