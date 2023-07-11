package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
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
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @InjectMocks
    private AccountService accService;

    @Mock
    private AccDAO userDAO;

    @Mock
    private Validator authValidator;

    @Mock
    private JwtUtil jwtUtil;


    @Test
    void testRegisterUser_SuccessfulRegistration() {
        // Arrange
        String username = "testUser";
        String password = "testPassword";
        String email = "test@example.com";

        when(authValidator.checkRegisterErrors(username, password, email)).thenReturn(List.of());

        // Act
        ResponseEntity<?> response = accService.addAccount(username, password, email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("successful"));
    }

    @Test
    void testRegisterUser_RegistrationErrors() {
        // Arrange
        String username = "testUser";
        String password = "testPassword";
        String email = "test@example.com";
        List<String> errors = List.of("Username is not available", "Already registered with this Email");

        when(authValidator.checkRegisterErrors(username, password, email)).thenReturn(errors);

        // Act
        ResponseEntity<?> response = accService.addAccount(username, password, email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("successful"));
        assertEquals("Username is not available", responseBody.get("usernameErrorClass"));
        assertNull(responseBody.get("passwordErrorClass"));
        assertEquals("Already registered with this Email", responseBody.get("emailErrorClass"));
    }

}