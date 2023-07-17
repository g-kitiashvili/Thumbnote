package com.example.Thumbnote.controller;

import com.example.Thumbnote.annotation.Secure;
import com.example.Thumbnote.service.AccountService;
import com.example.Thumbnote.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api")
@Secure(value = false)
public class AuthController {
    private final AuthService authService;
    private final AccountService accService;

    @Autowired
    public AuthController(AuthService authService, AccountService accService) {
        this.authService = authService;
        this.accService = accService;
    }
    @GetMapping("/token")
//    @Secure
    public String getToken(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return token;}
        else return null;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam("username") String username,
                                          @RequestParam("password") String password,
                                          @RequestParam("email") String email) {
            boolean success=accService.addAccount(username,password,email);
            if(success){
            return ResponseEntity.ok(HttpStatus.OK);}
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestParam("username") String username,
                                          @RequestParam("password") String password) {
        try {
            return ResponseEntity.ok(authService.authenticate(username, password));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> start(@RequestHeader("Authorization") String authHeader) {
        try {
            authService.logout(authHeader);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
