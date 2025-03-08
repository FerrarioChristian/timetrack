package com.timetrack.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(User user, HttpServletRequest request) {
        User newUser = new User(
                user.getUsername(),
                passwordEncoder.encode(user.getPassword())
        );
        userRepository.save(newUser);
        autoLogin(user, request);
    }

    @Override
    public User findById(String username) {
       return userRepository.findByUsername(username);
    }

    @Override
    public void autoLogin(User user, HttpServletRequest request) {
       try {
           request.login(user.getUsername(), user.getPassword());
       } catch (Exception e) {
           e.printStackTrace();
       }
    }
}
