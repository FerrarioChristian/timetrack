package com.timetrack.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for handling authentication-related requests,
 * including user registration and login.
 */
@Controller
public class AuthController {

    private final UserService userService;

    /**
     * Constructor for injecting the UserService dependency.
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Handles the GET request for the login page.
     * 
     * @return the registration thymeleaf template
     */
    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return "redirect:/";
        }
        return "login";
    }

    /**
     * Handles the GET request for the registration page.
     * 
     * @param model the model to be used in the view
     * @return the registration thymeleaf template
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return "redirect:/";
        }
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    /**
     * Handles the POST request for registering a new user.
     * 
     * @param user the user to be registered
     * @param request the HTTP request
     * @return the login page
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user, HttpServletRequest request) {
        User existingUser = userService.findById(user.getUsername());
        if (existingUser != null) {
            return "redirect:/register?error";
        }
        userService.registerUser(user, request);
        return "redirect:/";
    }

}
