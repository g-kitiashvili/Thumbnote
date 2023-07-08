package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.objects.Acc;
import com.example.Thumbnote.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccDAO accDAO;

    @Autowired
    public AccountService(AccDAO accDAO) {
        this.accDAO = accDAO;
    }

    public boolean addAccount(String username, String password, String email) {
        String hash = SecurityUtils.hashPassword(password);
        return accDAO.addAccount(username, hash, email);
    }

    public long getUserID(String username) {
        return accDAO.getUserID(username);
    }
}