package com.jack.userservice.security;

import com.jack.userservice.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateRequest_WhenValidTokenProvided() throws ServletException, IOException {
        // Arrange
        String token = "valid-token";
        Authentication authentication = mock(Authentication.class);

        request.addHeader(SecurityConstants.AUTHORIZATION_HEADER, SecurityConstants.BEARER_PREFIX + token);

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(token)).thenReturn(authentication);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider, times(1)).validateToken(token);
        verify(jwtTokenProvider, times(1)).getAuthentication(token);
        verify(filterChain, times(1)).doFilter(request, response);

        Authentication authInContext = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authInContext)
                .isNotNull()
                .isEqualTo(authentication);
    }

    @Test
    void shouldNotAuthenticateRequest_WhenInvalidTokenProvided() throws ServletException, IOException {
        // Arrange
        String token = "invalid-token";

        request.addHeader(SecurityConstants.AUTHORIZATION_HEADER, SecurityConstants.BEARER_PREFIX + token);

        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider, times(1)).validateToken(token);
        verify(jwtTokenProvider, times(0)).getAuthentication(anyString());
        verify(filterChain, times(1)).doFilter(request, response);

        Authentication authInContext = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authInContext).isNull();
    }

    @Test
    void shouldNotAuthenticateRequest_WhenNoTokenProvided() throws ServletException, IOException {
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider, times(0)).validateToken(anyString());
        verify(jwtTokenProvider, times(0)).getAuthentication(anyString());
        verify(filterChain, times(1)).doFilter(request, response);

        Authentication authInContext = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authInContext).isNull();
    }

    @Test
    void shouldHandleExceptionDuringAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid-token";

        request.addHeader(SecurityConstants.AUTHORIZATION_HEADER, SecurityConstants.BEARER_PREFIX + token);

        when(jwtTokenProvider.validateToken(token)).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider, times(1)).validateToken(token);
        verify(jwtTokenProvider, times(0)).getAuthentication(anyString());
        verify(filterChain, times(1)).doFilter(request, response);

        Authentication authInContext = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authInContext).isNull();  // No authentication should occur
    }
}
