package com.timetrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class.
 * Configures the security of the application.
 * The application uses form login with a custom login page.
 * The application uses BCryptPasswordEncoder to encode passwords.
 * The application uses method security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    /**
     * Configures the security of the application.
     * @param http HttpSecurity object.
     * @return SecurityFilterChain object.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/styles/**").permitAll()
                .requestMatchers("/login**", "/register**").anonymous()
                .anyRequest().authenticated()
        );
        http.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).permitAll());
        http.logout(logout -> logout.logoutSuccessUrl("/login?logout"));

        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}