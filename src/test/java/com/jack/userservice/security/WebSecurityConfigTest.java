package com.jack.userservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(WebSecurityConfig.class)
class WebSecurityConfigTest {

    @Autowired
    private WebSecurityConfig webSecurityConfig;

    @Autowired
    private AuthenticationManager authenticationManager; // Automatically configured by Spring Security

    @Test
    void testPasswordEncoderBean() {
        PasswordEncoder passwordEncoder = webSecurityConfig.passwordEncoder();
        assertNotNull(passwordEncoder, "PasswordEncoder bean should not be null");
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder, "PasswordEncoder bean should be an instance of BCryptPasswordEncoder");
    }

    @Test
    void testDaoAuthenticationProviderBean() {
        DaoAuthenticationProvider authProvider = webSecurityConfig.daoAuthenticationProvider();
        assertNotNull(authProvider, "DaoAuthenticationProvider bean should not be null");
        assertInstanceOf(DaoAuthenticationProvider.class, authProvider, "AuthenticationProvider bean should be an instance of DaoAuthenticationProvider");
    }

    @Test
    void testAuthenticationManagerBean() {
        assertNotNull(authenticationManager, "AuthenticationManager bean should not be null");
    }
}
