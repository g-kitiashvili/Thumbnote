package com.example.Thumbnote.interceptor;

import com.example.Thumbnote.service.AccountService;
import com.example.Thumbnote.service.AuthService;
import com.example.Thumbnote.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final AccountService accService;

    @Autowired
    public JwtRequestInterceptor(JwtUtil jwtUtil, AuthService authService, AccountService accService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.accService = accService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = authService.getUsernameFromToken(token);

            if (jwtUtil.validateToken(token, username)) {
                long userId = accService.getUserID(username);
                request.setAttribute("userID", userId);
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}