package com.timetrack.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling authentication-related requests,
 * including user registration and login.
 */
@Controller
public class AuthController {



    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
