package com.maxdev.kchan.security;

import com.maxdev.kchan.models.Credential;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

/**
 * Created by ytati
 * on 05.04.2024.
 */
public class PersonalAccountAuthorizationManager<T> implements AuthorizationManager<T> {


    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, T object) {
//        System.out.println(">>" + authentication.get());
//        System.out.println(">>" + object);
        RequestAuthorizationContext context = (RequestAuthorizationContext) object;

        int requestedId = Integer.parseInt(context.getRequest().getRequestURI().split("/")[3]);
        Authentication auth = authentication.get();
        if(!auth.isAuthenticated())
            return new AuthorizationDecision(false);
        if(auth.getAuthorities().contains("ADMIN"))
            return new AuthorizationDecision(true);
        Object principal = auth.getPrincipal();
        if(!(principal instanceof Credential))
            return null;
        Credential credential = (Credential) principal;
//        if(credential.getUsercard().getIsAdmin())
        return null;
    }
}
