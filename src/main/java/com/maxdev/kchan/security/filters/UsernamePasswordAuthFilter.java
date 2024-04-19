package com.maxdev.kchan.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Created by ytati
 * on 23.03.2024.
 * @deprecated all logics in /rest/auth/login endpoint
 */
public class UsernamePasswordAuthFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().startsWith("/rest/auth") && request.getMethod().equals(HttpMethod.POST.toString())) {
//            CredentialsDto credentialsDto = MAPPER.readValue(request.getInputStream(), CredentialsDto.class);
//            request.getParameter("email");
//            request.getParameter("password");
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(request.getParameter("email"), request.getParameter("password")));
        }

        filterChain.doFilter(request, response);
    }
}
