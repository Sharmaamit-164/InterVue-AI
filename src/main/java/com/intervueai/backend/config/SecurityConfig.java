package com.intervueai.backend.config;

import com.intervueai.backend.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =========================
                // CSRF
                // =========================
                .csrf(csrf -> csrf.disable())

                // =========================
                // JWT - Stateless
                // =========================
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =========================
                // Authorization
                // =========================
                .authorizeHttpRequests(auth -> auth

                        // -------------------------
                        // Authentication APIs
                        // -------------------------
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // -------------------------
                        // Swagger / OpenAPI
                        // -------------------------
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // -------------------------
                        // Resume APIs
                        // -------------------------
                        .requestMatchers(
                                "/api/resumes/**"
                        ).authenticated()

                        // -------------------------
                        // Job APIs
                        // -------------------------
                        .requestMatchers(
                                "/api/jobs/**"
                        ).authenticated()

                        // -------------------------
                        // User APIs
                        // -------------------------
                        .requestMatchers(
                                "/api/users/**"
                        ).authenticated()

                        // -------------------------
                        // Everything else
                        // -------------------------
                        .anyRequest().authenticated()
                )

                // =========================
                // JWT Filter
                // =========================
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}