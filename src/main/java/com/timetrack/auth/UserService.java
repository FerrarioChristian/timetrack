package com.timetrack.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Service interface for User entity.
 */
public interface UserService {
    void registerUser(User user, HttpServletRequest request);

    User findById(String username);

    void autoLogin(@Valid User user, HttpServletRequest request);
}
