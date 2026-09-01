package com.intervueai.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        System.out.println("======================================");
        System.out.println("JWT FILTER");
        System.out.println("Request: " + request.getMethod()
                + " " + request.getRequestURI());
        System.out.println("Authorization Header Present: "
                + (authorizationHeader != null));

        String email = null;
        String token = null;

        if (authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")) {

            token = authorizationHeader.substring(7);

            try {

                email = jwtUtil.extractEmail(token);

                System.out.println("JWT Email: " + email);

                if (jwtUtil.isTokenValid(token)) {

                    UserDetails userDetails =
                            userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    System.out.println(
                            "JWT AUTHENTICATION SUCCESS"
                    );

                    System.out.println(
                            "Authorities: "
                                    + userDetails.getAuthorities()
                    );

                } else {

                    System.out.println(
                            "JWT TOKEN INVALID"
                    );
                }

            } catch (Exception e) {

                System.out.println(
                        "JWT ERROR: " + e.getMessage()
                );

                e.printStackTrace();
            }

        } else {

            System.out.println(
                    "NO BEARER TOKEN FOUND"
            );
        }

        System.out.println(
                "Authenticated: "
                        + (SecurityContextHolder
                        .getContext()
                        .getAuthentication() != null)
        );

        System.out.println("======================================");

        filterChain.doFilter(request, response);
    }
}