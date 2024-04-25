package com.maxdev.kchan.security;

import com.maxdev.kchan.models.Credential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * Created by ytati
 * on 23.03.2024.
 */
@Slf4j
public class UserAuthenticationProvider implements AuthenticationProvider {
    CredentialsService credentialsService;

    public UserAuthenticationProvider(CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Credential userDto = null;

        try {
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                // authentication by username and password
                userDto = credentialsService.authenticate((String) authentication.getPrincipal(), (String) authentication.getCredentials());
            } else if (authentication instanceof PreAuthenticatedAuthenticationToken) {
                // authentication by cookie
                userDto = credentialsService.findByToken((String) authentication.getPrincipal());
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        if (userDto == null) {
            throw new BadCredentialsException("Authentication failure");
        }

        // pass all credentials as first arg to get it in controllers by @AuthenticationPrincipal annotation
        return new UsernamePasswordAuthenticationToken(userDto, null, credentialsService.getUserAuthorities(userDto));
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}

