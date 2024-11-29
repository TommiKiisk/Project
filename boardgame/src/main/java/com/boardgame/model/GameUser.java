package com.boardgame.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
public class GameUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password; // Password should be securely hashed (e.g., BCrypt)
    private String role;

    // Default no-arg constructor for JPA
    public GameUser() {
    }

    // Constructor with arguments for easy initialization
    public GameUser(String username, String password, String role) {
        this.username = username;
        this.password = hashPassword(password); // Hashing password before storing
        this.role = role;
    }

    // Hashing the password using BCrypt
    private String hashPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password); // Hashes and returns the password
    }

    // Method to check if the provided password matches the stored (hashed) password
    public boolean checkPassword(String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, this.password); // Compares raw password with hashed password
    }

    // Getters and Setters
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
        return password;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password); // Always hash when setting a new password
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Override toString to exclude sensitive information like password
    @Override
    public String toString() {
        return "GameUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
