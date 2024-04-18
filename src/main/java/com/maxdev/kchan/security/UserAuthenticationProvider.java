package com.maxdev.kchan.security;

import com.maxdev.kchan.models.Credential;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * Created by ytati
 * on 23.03.2024.
 */
public class UserAuthenticationProvider implements AuthenticationProvider {
    CredentialsService credentialsService;

    public UserAuthenticationProvider(CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Credential userDto = null;
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            // authentication by username and password
            userDto = credentialsService.authenticate((String) authentication.getPrincipal(), (String) authentication.getCredentials());
        } else if (authentication instanceof PreAuthenticatedAuthenticationToken) {
            // authentication by cookie
            userDto = credentialsService.findByToken((String) authentication.getPrincipal());
        }

        if (userDto == null) {
            return null;
        }
        // pass all credentials as first arg to get it in controllers by @AuthenticationPrincipal annotation
        return new UsernamePasswordAuthenticationToken(userDto, null, credentialsService.getUserAuthorities(userDto));
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}

