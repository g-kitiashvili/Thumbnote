package com.example.Thumbnote.service;

import com.example.Thumbnote.utils.JwtUtil;
import com.example.Thumbnote.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final Validator valid;

    @Autowired
    public AuthService( JwtUtil jwtUtil, Validator valid) {
        this.jwtUtil = jwtUtil;
        this.valid = valid;
    }


    public Map<String, Object> authenticate(String username, String password) {
        if (valid.validLogin(username, password)) {
            String jwt = jwtUtil.generateToken(username);

            return Map.of("token", jwt);
        } else {
            throw new RuntimeException("Invalid login credentials.");
        }
    }




}