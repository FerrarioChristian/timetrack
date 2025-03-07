package com.timetrack.auth;

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
    public void save(User user) {
        User newUser = new User(
                user.getUsername(),
                passwordEncoder.encode(user.getPassword())
        );
        userRepository.save(newUser);
    }

    @Override
    public User findById(String username) {
       return userRepository.findByUsername(username);
    }
}
