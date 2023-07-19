package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.utils.SecurityUtils;
import com.example.Thumbnote.utils.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @InjectMocks
    private AccountService accService;

    @Mock
    private Validator authValidator;

    @Mock
    private AccDAO accDAO;

    @Test
    void testAddAccount_SuccessfulRegistration() {
        // Arrange
        String username = "testUser";
        String password = "testPassword";
        String email = "test@example.com";

        when(authValidator.checkRegisterErrors(username, password, email)).thenReturn(List.of());
        when(accDAO.addAccount(username, SecurityUtils.hashPassword(password), email)).thenReturn(true);

        // Act
        boolean result = accService.addAccount(username, password, email);

        // Assert
        assertTrue(result);
    }

    @Test
    void testAddAccount_RegistrationErrors() {
        // Arrange
        String username = "testUser";
        String password = "testPassword";
        String email = "test@example.com";
        List<String> errors = List.of("Username is not available", "Already registered with this Email");

        when(authValidator.checkRegisterErrors(username, password, email)).thenReturn(errors);

        // Act
        boolean result = accService.addAccount(username, password, email);

        // Assert
        assertFalse(result);
    }
}