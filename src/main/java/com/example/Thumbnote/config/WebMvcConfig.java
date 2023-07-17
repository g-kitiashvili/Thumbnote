package com.example.Thumbnote.config;


import com.example.Thumbnote.interceptor.JwtRequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtRequestInterceptor jwtRequestInterceptor;

    public WebMvcConfig(@Lazy JwtRequestInterceptor jwtRequestInterceptor) {
        this.jwtRequestInterceptor = jwtRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtRequestInterceptor);
    }
}