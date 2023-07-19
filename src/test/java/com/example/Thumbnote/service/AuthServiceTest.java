package com.example.Thumbnote.service;

import com.example.Thumbnote.utils.JwtUtil;
import com.example.Thumbnote.utils.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private Validator authValidator;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    void testAuthenticate_ValidCredentials() {
        String username = "testUser";
        String password = "testPassword";
        String jwtToken = "testJwtToken";

        when(authValidator.validLogin(username, password)).thenReturn(true);
        when(jwtUtil.generateToken(username)).thenReturn(jwtToken);

        Map<String, Object> response = authService.authenticate(username, password);

        assertNotNull(response);
        assertEquals(jwtToken, response.get("token"));
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        String username = "testUser";
        String password = "testPassword";

        when(authValidator.validLogin(username, password)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate(username, password);
        });

        assertEquals("Invalid login credentials.", exception.getMessage());
    }
}