package com.example.Thumbnote.service;

import com.example.Thumbnote.utils.JwtUtil;
import com.example.Thumbnote.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;
    private final Validator valid;

    @Autowired
    public AuthService(AccountService accountService, JwtUtil jwtUtil, Validator valid) {
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
        this.valid = valid;
    }


    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    public Map<String, Object> authenticate(String username, String password) {
        if (valid.validLogin(username, password)) {
            String jwt = jwtUtil.generateToken(username);

            return Map.of("token", jwt);
        } else {
            throw new RuntimeException("Invalid login credentials.");
        }
    }


    public void logout(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        if (jwtUtil.validateToken(token, username)) {
            jwtUtil.invalidateToken(token);
        } else {
            throw new RuntimeException("Invalid token.");
        }
    }

}