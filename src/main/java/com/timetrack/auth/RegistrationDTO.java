package com.timetrack.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

/**
 * Data Transfer Object (DTO) used for handling registration data.
 * Contains fields for user credentials: email and password.
 */

public class RegistrationDTO {
    /**
     * Unique identifier for the user. This is typically set by the database,
     * so it does not need to be provided during registration.
     */
    private long id;

    /**
     * The email address of the user.
     * This field is validated to ensure it's a valid email format.
     * It cannot be empty.
     */
    @NotEmpty(message = "Should not be empty")
    @Email(message = "Use a valid email address")
    private String email;

    /**
     * The password chosen by the user during registration.
     * This field cannot be empty.
     */
    @NotEmpty(message = "Should not be empty")
    private String password;

}
