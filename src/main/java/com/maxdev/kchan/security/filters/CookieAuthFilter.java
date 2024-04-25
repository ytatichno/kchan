package com.maxdev.kchan.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by ytati
 * on 23.03.2024.
 * @deprecated
 */
//@Component
public class CookieAuthFilter extends BasicAuthenticationFilter {



    public static final String AUTH_COOKIE_NAME = "auth-cookie";
//    public static

//    @Autowired
    public CookieAuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            Optional<Cookie> cookieAuth = Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                    .filter(cookie -> AUTH_COOKIE_NAME.equals(cookie.getName()))
                    .findFirst();
            logger.debug(request.getRequestURI() +" : " + cookieAuth/*.orElseThrow().getValue()*/);

            if(cookieAuth.isEmpty()){
                filterChain.doFilter(request, response);
                return;
            }


            PreAuthenticatedAuthenticationToken authRequest = new PreAuthenticatedAuthenticationToken("13&ow@mail.ru&pK7mKFIiS4AsDKiduSREPbLH81fY3XqPAN2orRyCmo1xBNW2jk7wneEPxp6Z7OdGMNyINC4WFgIMdR+K4r33Og==", null);
//                PreAuthenticatedAuthenticationToken authRequest = new PreAuthenticatedAuthenticationToken(cookieAuth.get().getValue(), null);
            logger.debug("Found username " + cookieAuth.get().getValue().split("&", 3)[1]);
//            if (authenticationIsRequired(username)) {
//                Authentication authResult = this.authenticationManager.authenticate(authRequest);
//                SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
//                context.setAuthentication(authResult);
//                this.securityContextHolderStrategy.setContext(context);
//                if (this.logger.isDebugEnabled()) {
//                    this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authResult));
//                }
//                this.rememberMeServices.loginSuccess(request, response, authResult);
//                this.securityContextRepository.saveContext(context, request, response);
//                onSuccessfulAuthentication(request, response, authResult);
//            }
//                SecurityContextHolder.getContext().setAuthentication(authRequest);
//                SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(authRequest));


        } catch (AuthenticationException e) {
            logger.error(e);
        }


        filterChain.doFilter(request, response);
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        Optional<Cookie> cookieAuth = Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
//                .filter(cookie -> AUTH_COOKIE_NAME.equals(cookie.getName()))
//                .findFirst();
//
//        if (cookieAuth.isPresent()
//                /*&& (
//                        SecurityContextHolder.getContext().getAuthentication() == null
//                        || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
//                )*/
//        ) {
//            Authentication preAuth = new PreAuthenticatedAuthenticationToken(cookieAuth.get().getValue(), null);
//            preAuth.setAuthenticated(true);
//            // bebe
////            String hackerString = cookieAuth.get().getValue().substring(0, 20);
////            Authentication preAuth = new PreAuthenticatedAuthenticationToken(hackerString, null);
////            preAuth.setAuthenticated(true);
//
//            Authentication authentication = null;
////            try {
//                authentication = this.authManager.authenticate(preAuth);
////            }finally {
////                if(authentication != null)
////                    SecurityContextHolder.getContext().setAuthentication(authentication);
//////                filterChain.doFilter(request, response);
////
////            }
//        } else {
//            filterChain.doFilter(request, response);
//        }
////        filterChain.doFilter(request, response);
//
//
////        cookieAuth.ifPresent(cookie -> SecurityContextHolder.getContext().setAuthentication(
////                new PreAuthenticatedAuthenticationToken(cookie.getValue(), null)));
//
//    }
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if(applied)
//            filterChain.doFilter(request, response);
//        applied = true;
//        Optional<Cookie> cookieAuth = Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
//                .filter(cookie -> AUTH_COOKIE_NAME.equals(cookie.getName()))
//                .findFirst();
//
//        if (cookieAuth.isPresent()
//                /*&& (
//                        SecurityContextHolder.getContext().getAuthentication() == null
//                        || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
//                )*/
//        ) {
//            Authentication preAuth = new PreAuthenticatedAuthenticationToken(cookieAuth.get().getValue(), null);
//            preAuth.setAuthenticated(true);
//            // bebe
////            String hackerString = cookieAuth.get().getValue().substring(0, 20);
////            Authentication preAuth = new PreAuthenticatedAuthenticationToken(hackerString, null);
////            preAuth.setAuthenticated(true);
//
//            Authentication authentication = null;
////            try {
//                authentication = this.authManager.authenticate(preAuth);
////            }finally {
////                if(authentication != null)
////                    SecurityContextHolder.getContext().setAuthentication(authentication);
//////                filterChain.doFilter(request, response);
////
////            }
//        } else {
//            filterChain.doFilter(request, response);
//        }
////        filterChain.doFilter(request, response);
//
//
////        cookieAuth.ifPresent(cookie -> SecurityContextHolder.getContext().setAuthentication(
////                new PreAuthenticatedAuthenticationToken(cookie.getValue(), null)));
//
//    }

//    @Override
//    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
//        Optional<Cookie> cookieAuth = Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
//                .filter(cookie -> AUTH_COOKIE_NAME.equals(cookie.getName()))
//                .findFirst();
//
//        if(cookieAuth.isEmpty())
//            return null;  // or anonymousUser
//
//        Authentication preAuth = new PreAuthenticatedAuthenticationToken(cookieAuth.get().getValue(), null);
//        preAuth.setAuthenticated(true);
//        // bebe
////            String hackerString = cookieAuth.get().getValue().substring(0, 20);
////            Authentication preAuth = new PreAuthenticatedAuthenticationToken(hackerString, null);
////            preAuth.setAuthenticated(true);
//
//        Authentication authentication = this.authManager.authenticate(preAuth);
//        return authentication.getPrincipal();
//    }
//
//    @Override
//    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
//        return null;
//    }
}
