package com.example.Thumbnote.controller;



import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.objects.Acc;
import com.example.Thumbnote.utils.Validator;
import com.example.Thumbnote.utils.JwtUtil;
import com.example.Thumbnote.utils.SecurityUtils;
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
    private final AccDAO accDAO;
    private final Validator validator;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AccDAO accDAO, Validator validator, JwtUtil jwtUtil) {
        this.accDAO = accDAO;
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@ModelAttribute("signUpAccountInputForm") Acc account) {
        Map<String, Object> response = new HashMap<>();
        List<String> errors = validator.checkRegisterErrors(account.getUsername(), account.getPassword_hash(), account.getEmail());

        if (!errors.isEmpty()) {
            response.put("successful", false);
            response.put("usernameErrorClass", validator.getRegisterErrorClass("username", errors));
            response.put("passwordErrorClass", validator.getRegisterErrorClass("password", errors));
            response.put("emailErrorClass", validator.getRegisterErrorClass("email", errors));
            return ResponseEntity.badRequest().body(response);
        }

        String hash = SecurityUtils.hashPassword(account.getPassword_hash());
        boolean success = accDAO.addAccount(account.getUsername(), hash, account.getEmail());
        response.put("successful", success);

        if (!success) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestParam("username") String username,
                                          @RequestParam("password") String password) {
        if (validator.validLogin(username, password)) {
            String jwt = jwtUtil.generateToken(username);
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}