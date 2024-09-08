package com.jack.userservice.service;

import com.jack.userservice.entity.Users;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserService {
    Users registerUser(Users users);
    Optional<Users> updateUser(Long id, Users users);
    void deleteUser(Long id);
    Users login(String email, String password);
    Optional<Users> getUserById(Long id);
    Optional<Users> findByEmail(String email);
    boolean verifyPassword(String rawPassword, String encodedPassword);
    public UserDetails loadUserByEmail(String email);
}
