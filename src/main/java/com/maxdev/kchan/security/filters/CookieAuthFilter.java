package com.maxdev.kchan.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by ytati
 * on 23.03.2024.
 */
public class CookieAuthFilter extends OncePerRequestFilter {
    public static final String AUTH_COOKIE_NAME = "auth-cookie";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<Cookie> cookieAuth = Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> AUTH_COOKIE_NAME.equals(cookie.getName()))
                .findFirst();

        cookieAuth.ifPresent(cookie -> SecurityContextHolder.getContext().setAuthentication(
                new PreAuthenticatedAuthenticationToken(cookie.getValue(), null)));

        filterChain.doFilter(request, response);
    }
}
