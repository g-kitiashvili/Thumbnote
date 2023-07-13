package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.service.AuthService;
import com.example.Thumbnote.utils.JwtUtil;
import com.example.Thumbnote.utils.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        ResponseEntity<?> response = authService.authenticate(username, password);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(jwtToken, responseBody.get("token"));
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        String username = "testUser";
        String password = "testPassword";

        when(authValidator.validLogin(username, password)).thenReturn(false);

        ResponseEntity<?> response = authService.authenticate(username, password);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid login credentials.", responseBody.get("error"));
    }

    @Test
    void testLogout_ValidToken() {
        String authHeader = "Bearer testToken";
        String token = "testToken";
        String username = "testUser";

        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(true);

        ResponseEntity<?> response = authService.logout(authHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testLogout_InvalidToken() {
        String authHeader = "Bearer testToken";
        String token = "testToken";
        String username = "testUser";

        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(false);

        ResponseEntity<?> response = authService.logout(authHeader);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid Token", responseBody.get("error"));
    }
}