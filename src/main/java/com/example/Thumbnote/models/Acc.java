package com.example.Thumbnote.models;

import jakarta.persistence.*;

import java.util.Objects;


@Entity
@Table(name = "users")
public class Acc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String hash_password;

    @Column(nullable = false, unique = true)
    private String email;

    // Default constructor

    // Constructor with parameters
    public Acc(String username, String password, String email) {
        this.username = username;
        this.hash_password = password;
        this.email = email;
    }

    public Acc() {

    }


    // Getters and setters for all attributes
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return hash_password;
    }

    public void setPassword(String password) {
        this.hash_password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Acc user = (Acc) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password_hash='" + hash_password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}