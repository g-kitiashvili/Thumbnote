package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.utils.SecurityUtils;
import com.example.Thumbnote.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {

    private final AccDAO accDAO;
    private final Validator authValidator;

    @Autowired
    public AccountService(AccDAO accDAO, Validator authValidator) {
        this.accDAO = accDAO;
        this.authValidator = authValidator;
    }

    public ResponseEntity<?> addAccount(String username, String password, String email) {
        Map<String, Object> response = new HashMap<>();
        List<String> errors = authValidator.checkRegisterErrors(username, password, email);

        if (!errors.isEmpty()) {
            response.put("successful", false);
            response.put("usernameErrorClass", Validator.getRegisterErrorClass("username", errors));
            response.put("passwordErrorClass", Validator.getRegisterErrorClass("password", errors));
            response.put("emailErrorClass", Validator.getRegisterErrorClass("email", errors));

            return ResponseEntity.ok(response);
        }

        String hash = SecurityUtils.hashPassword(password);
        accDAO.addAccount(username, hash, email);
        response.put("successful", true);


        return ResponseEntity.ok(response);

    }

    public long getUserID(String username) {
        return accDAO.getUserID(username);
    }
}