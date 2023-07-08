package com.example.Thumbnote.service;

import com.example.Thumbnote.utils.JwtUtil;
import com.example.Thumbnote.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(AccountService accountService, JwtUtil jwtUtil) {
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
    }

    public String generateToken(String username, String password) {
            return jwtUtil.generateToken(username);


    }

    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    public boolean isValidToken(String token, String username) {
        String tokenUsername = jwtUtil.getUsernameFromToken(token);
        return username.equals(tokenUsername);
    }
}