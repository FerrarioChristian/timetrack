package com.timetrack.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

/**
 * Entity class for the User table in the database.
 * Contains the user's username and password.
 */
@Entity
@Table(name = "`user`")
public class User {
    /**
     * The user's unique identifier.
     * This is the primary key in the database and is auto-generated when a new user is created.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user's username.
     * This is a unique field in the database and is used for logging in.
     */
    @NotEmpty
    private String username;

    /**
     * The user's password.
     * This is a field in the database and is used for logging in.
     */
    @NotEmpty
    private String password;

    /**
     * Constructor for creating a new User object.
     * 
     * @param username the user's username
     * @param password the user's password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
