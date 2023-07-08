package com.example.Thumbnote.controller;

import com.example.Thumbnote.service.AccountService;
import com.example.Thumbnote.service.AuthService;
import com.example.Thumbnote.utils.Validator;
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
        Map<String, Object> response = new HashMap<>();
        List<String> errors = validator.checkRegisterErrors(username, password, email);

        if (!errors.isEmpty()) {
            response.put("successful", false);
            response.put("usernameErrorClass", validator.getRegisterErrorClass("username", errors));
            response.put("passwordErrorClass", validator.getRegisterErrorClass("password", errors));
            response.put("emailErrorClass", validator.getRegisterErrorClass("email", errors));
            return ResponseEntity.badRequest().body(response);
        }

        boolean success = accountService.addAccount(username, password, email);
        response.put("successful", success);

        if (!success) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestParam("username") String username,
                                          @RequestParam("password") String password) {
        if(validator.validLogin(username,password)){
            String jwt = authService.generateToken(username, password);
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return ResponseEntity.ok(response);
        } else  {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}