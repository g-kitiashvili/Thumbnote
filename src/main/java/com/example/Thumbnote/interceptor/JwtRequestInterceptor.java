package com.example.Thumbnote.interceptor;

import com.example.Thumbnote.annotation.Secure;
import com.example.Thumbnote.utils.JwtUtil;
import com.sun.istack.NotNull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.example.Thumbnote.dao.AccDAO;


@Component
@Secure(value = false)
public class JwtRequestInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final AccDAO userDAO;

    private final Secure noAuth;

    private final RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public JwtRequestInterceptor(RequestMappingHandlerMapping handlerMapping, JwtUtil jwtUtil, AccDAO userDAO) {
        this.handlerMapping = handlerMapping;
        noAuth = this.getClass().getAnnotation(Secure.class);
        this.jwtUtil = jwtUtil;
        this.userDAO = userDAO;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        Secure controllerClass = getAnnotation(request);
        if (controllerClass == null){
            return false;
        }

        if (controllerClass.value()){
            // check if request is authorized:
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.replace("Bearer ", "");
                String username = jwtUtil.getUsernameFromToken(token);
                if (username != null && jwtUtil.validateToken(token, username)) {
                    Long userId = userDAO.getUserID(username);
                    RequestContextHolder.currentRequestAttributes().setAttribute("userId", userId, RequestAttributes.SCOPE_REQUEST);
                    return true;
                }
            }
        } else {
            // if the request does not need authorization
            return true;
        }

        response.setStatus(401);
        return false;
    }

    private Secure getAnnotation(HttpServletRequest request) throws Exception {
        HandlerExecutionChain handler = handlerMapping.getHandler(request);
        if (handler != null) {
            Object realHandler = handler.getHandler();
            if (realHandler instanceof HandlerMethod handlerMethod) {
                Secure annotation = handlerMethod.getMethodAnnotation(Secure.class);
                if (annotation != null) {
                    return annotation;
                }
                annotation = handlerMethod.getBeanType().getAnnotation(Secure.class);
                if (annotation != null) {
                    return annotation;
                }
                return noAuth;
            }
        }

        return null; //in this case there is no handler for that request which is 404 not found error
    }
}