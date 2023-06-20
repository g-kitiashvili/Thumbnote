package com.example.Thumbnote.config;


import com.example.Thumbnote.dao.UserDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class AppConfiguration {

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/thumbnote_db");
        dataSource.setUsername("user");
        dataSource.setPassword("password");
        return dataSource;
    }
    @Bean
    public UserDAO userDAO(DataSource dataSource){
        return new UserDAO(dataSource);
    }
}
