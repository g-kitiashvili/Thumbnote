package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.utils.SecurityUtils;
import com.example.Thumbnote.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccDAO accDAO;
    private final Validator authValidator;

    @Autowired
    public AccountService(AccDAO accDAO, Validator authValidator) {
        this.accDAO = accDAO;
        this.authValidator = authValidator;
    }

    public boolean addAccount(String username, String password, String email) {
        List<String> errors = authValidator.checkRegisterErrors(username, password, email);

        if (!errors.isEmpty()) {

            return false;
        }

        String hash = SecurityUtils.hashPassword(password);
        accDAO.addAccount(username, hash, email);


        return true;

    }
}