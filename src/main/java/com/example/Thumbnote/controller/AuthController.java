package com.example.Thumbnote.controller;

import com.example.Thumbnote.service.AccountService;
import com.example.Thumbnote.service.AuthService;
import com.example.Thumbnote.utils.Validator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api")
public class AuthController {

    private final AccountService accountService;
    private final AuthService authService;
    private final Validator validator;

    @Autowired
    public AuthController(AccountService accountService, AuthService authService, Validator validator) {
        this.accountService = accountService;
        this.authService = authService;
        this.validator = validator;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam("username") String username,
                                          @RequestParam("password") String password,
                                          @RequestParam("email") String email) {
        return accountService.addAccount(username, password, email);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestParam("username") String username,
                                          @RequestParam("password") String password) {
        return authService.authenticate(username, password);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> start(@RequestHeader("Authorization") String authHeader) {
        return authService.logout(authHeader);
    }
}