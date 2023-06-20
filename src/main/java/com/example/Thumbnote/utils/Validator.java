package com.example.Thumbnote.utils;

import com.example.Thumbnote.dao.AccDAO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

@Service
public class Validator {

        private static final String USERNAME_ERROR_MESSAGE = "Username is not available";
        private static final String PASSWORD_ERROR_MESSAGE = "Password is not strong";
        private static final String EMAIL_ERROR_MESSAGE = "Already registered with this Email";

        private  final AccDAO dao ;

        public Validator(AccDAO dao) {
            this.dao = dao;
        }

        private  boolean isUsernameAvailable(String username) {
            return dao.isAviable("username", username);
        }

        private  boolean isEmailAvailable(String email) {
            return dao.isAviable("email", email);
        }

        public  Boolean validLogin(String username, String password) {
            if (isUsernameAvailable(username)) {
                return false;
            }

            return Objects.equals(dao.getPassword(username), SecUtils.hashPassword(password));
        }

        public  List<String> canRegister(String username, String password, String email) {
            List<String> errors = new ArrayList<>();

            if (!isUsernameAvailable(username)) {
                errors.add(USERNAME_ERROR_MESSAGE);
            }
            if (!isEmailAvailable(email)) {
                errors.add(EMAIL_ERROR_MESSAGE);
            }

            return errors;
        }

        public static String registerError(String fieldName, List<String> errors) {
            if (Objects.equals(fieldName, "username")) {
                return errors.contains(USERNAME_ERROR_MESSAGE) ? USERNAME_ERROR_MESSAGE : null;
            } else if (Objects.equals(fieldName, "password")) {
                return errors.contains(PASSWORD_ERROR_MESSAGE)? PASSWORD_ERROR_MESSAGE : null;
            } else if (Objects.equals(fieldName, "email")) {
                return errors.contains(EMAIL_ERROR_MESSAGE)? EMAIL_ERROR_MESSAGE : null;
            }

            return "";
        }

}