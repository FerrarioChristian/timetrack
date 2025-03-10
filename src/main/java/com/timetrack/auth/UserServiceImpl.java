package com.timetrack.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for user registration and login.
 * Implements UserService interface.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for UserServiceImpl.
     * @param userRepository UserRepository
     * @param passwordEncoder PasswordEncoder
     */
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     * @param user User
     * @param request HttpServletRequest
     */
    @Override
    public void registerUser(User user, HttpServletRequest request) {
        User newUser = new User(
                user.getUsername(),
                passwordEncoder.encode(user.getPassword())
        );
        userRepository.save(newUser);
        autoLogin(user, request);
    }

    /**
     * Finds a user by username.
     * @param username String
     * @return User
     */
    @Override
    public User findById(String username) {
       return userRepository.findByUsername(username);
    }

    /**
     * Logs in a user.
     * @param user User
     * @param request HttpServletRequest
     */
    @Override
    public void autoLogin(User user, HttpServletRequest request) {
       try {
           request.login(user.getUsername(), user.getPassword());
       } catch (Exception e) {
           e.printStackTrace();
       }
    }
}
