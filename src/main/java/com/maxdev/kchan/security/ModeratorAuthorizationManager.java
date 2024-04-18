package com.maxdev.kchan.security;

import com.maxdev.kchan.models.Credential;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ytati
 * on 09.04.2024.
 */
public class ModeratorAuthorizationManager<T> implements AuthorizationManager<T> {


    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, T object) {

        RequestAuthorizationContext context = (RequestAuthorizationContext) object;
        Pattern pattern = Pattern.compile("\\d+");
        String url = context.getRequest().getRequestURL().toString();
        String uri = context.getRequest().getRequestURI();
        Matcher matcher = pattern.matcher(uri);
        if(!matcher.find())
            return null;
        int section_id = Integer.parseInt(uri.substring(matcher.start(), matcher.end()));
        Authentication auth = authentication.get();
        if(!auth.isAuthenticated())
            return new AuthorizationDecision(false);
        if(auth.getAuthorities().contains("ADMIN"))
            return new AuthorizationDecision(true);
        Object principal = auth.getPrincipal();
        if(!(principal instanceof Credential))
            return null;
        Credential credential = (Credential) principal;
        if(credential.getUsercard().getModerableSections().contains(section_id))
            return new AuthorizationDecision(true);
//        if(credential.getUsercard().getIsAdmin())
        return new AuthorizationDecision(false);
    }


}