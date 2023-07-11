package com.example.Thumbnote.config;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.dao.NoteDAO;
import com.example.Thumbnote.dao.NotebookDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class AppConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    @Bean
    public AccDAO userDAO(DataSource dataSource) {
        return new AccDAO(dataSource);
    }

    @Bean
    public NoteDAO noteDAO(DataSource dataSource) {
        return new NoteDAO(dataSource);
    }

    @Bean
    public NotebookDAO notebookDAO(DataSource dataSource) {
        return new NotebookDAO(dataSource);
    }

}