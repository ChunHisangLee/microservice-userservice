package com.jack.userservice.service;

import com.jack.userservice.entity.Users;

import java.util.Optional;

public interface UserService {

    /**
     * Registers a new user.
     *
     * @param users The user information to register.
     * @return The registered user.
     */
    Users registerUser(Users users);

    /**
     * Updates an existing user's information.
     *
     * @param id The ID of the user to update.
     * @param users The updated user information.
     * @return An Optional containing the updated user, if found.
     */
    Optional<Users> updateUser(Long id, Users users);

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     */
    void deleteUser(Long id);

    /**
     * Logs in a user with the given email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return The logged-in user.
     */
    Users login(String email, String password);

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return An Optional containing the user, if found.
     */
    Optional<Users> getUserById(Long id);

    /**
     * Finds a user by their email.
     *
     * @param email The email to search by.
     * @return An Optional containing the user, if found.
     */
    Optional<Users> findByEmail(String email);

    /**
     * Verifies that a raw password matches the encoded password.
     *
     * @param rawPassword The raw password to check.
     * @param encodedPassword The encoded password to check against.
     * @return True if the passwords match, false otherwise.
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);
}
