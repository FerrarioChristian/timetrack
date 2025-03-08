package com.timetrack.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface UserService {
    void registerUser(User user, HttpServletRequest request);

    User findById(String username);

    void autoLogin(@Valid User user, HttpServletRequest request);
}
