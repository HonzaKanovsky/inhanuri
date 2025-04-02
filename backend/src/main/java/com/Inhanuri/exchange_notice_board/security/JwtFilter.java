package com.Inhanuri.exchange_notice_board.security;

import com.Inhanuri.exchange_notice_board.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtFilter extends GenericFilterBean {
    private final JwtUtil jwtUtil;
    private final AuthService authService; // ✅ Inject Blacklist Check

    public JwtFilter(JwtUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix

            if (authService.isTokenBlacklisted(token)) { // ✅ Block blacklisted tokens
                SecurityContextHolder.clearContext();
                return;
            }

            String username = jwtUtil.extractUsername(token);
            if (jwtUtil.validateToken(token, username)) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }

        chain.doFilter(request, response);
    }
}
