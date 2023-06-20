package com.example.Thumbnote.controllers;

import com.example.Thumbnote.dao.AccDAO;

import com.example.Thumbnote.models.Acc;
import com.example.Thumbnote.utils.SecUtils;
import com.example.Thumbnote.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Account")
public class AuthController {

    private final AccDAO userDAO;
    private final Validator validator;

    @Autowired
    public AuthController(AccDAO userDAO, Validator validator) {
        this.userDAO = userDAO;
        this.validator = validator;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam("username") String username,
                                          @RequestParam("password") String password,
                                          @RequestParam("email") String email) {
        Map<String, Object> response = new HashMap<>();
        List<String> errors = validator.canRegister(username, password, email);

        if (!errors.isEmpty()) {
            response.put("successful", false);
            response.put("usernameErrorClass", Validator.registerError("username", errors));
            response.put("passwordErrorClass", Validator.registerError("password", errors));
            response.put("emailErrorClass", Validator.registerError("email", errors));
            return ResponseEntity.badRequest().body(response);
        }

        String hash = SecUtils.hashPassword(password);
        boolean success = AccDAO.addAccount(username, hash, email);
        response.put("successful", success);

        if (!success) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }






}