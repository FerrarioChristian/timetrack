package com.timetrack.auth;

public interface UserService {
    void save(User user);

    User findById(String username);
}
