package com.maxdev.kchan.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by ytati
 * on 22.03.2024.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    private final CredentialsService userDetailsServiceImpl;

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;
    private UserAuthenticationProvider authenticationProvider;

    //    @Autowired
//    public SecurityConfig(CredentialsService userDetailsServiceImpl) {
//        this.userDetailsServiceImpl = userDetailsServiceImpl;
//    }
    @Bean
    PasswordEncoder passwordEncoder() throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] secretKeyBytes = Objects.requireNonNull(secretKey).getBytes(StandardCharsets.UTF_8);
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA512");
        mac.init(secretKeySpec);
        return new PasswordEncoder() {
            final Mac macEncoder = mac;

            @Override
            public String encode(CharSequence rawPassword) {
                return new String(
                        Base64.getEncoder().encode(
                                mac.doFinal(
                                        rawPassword.toString().getBytes(StandardCharsets.UTF_8)
                                )
                        )
                );
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encode(rawPassword).equals(encodedPassword);
            }
        };
    }

//    @Autowired
//    CredentialsService credentialsService;
//    @Autowired
//    UserAuthenticationProvider userAuthenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, CredentialsService credentialsService) throws Exception {
        authenticationProvider = new UserAuthenticationProvider(credentialsService);
        ProviderManager manager = new ProviderManager(authenticationProvider);
        manager.setEraseCredentialsAfterAuthentication(false);
//        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
////        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
////        authenticationManagerBuilder.userDetailsService(credentialsService);
////        authenticationManagerBuilder.inMemoryAuthentication()
////                .withUser("memuser")
////                .password(passwordEncoder().encode("pass"))
////                .roles("USER");
//        return authenticationManagerBuilder.build();

        return manager;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager, OncePerRequestFilter authenticationFilter/*, CredentialsService credentialsService*/) throws Exception {
        PersonalAccountAuthorizationManager<RequestAuthorizationContext> personalAccountAccess = new PersonalAccountAuthorizationManager<>();
        ModeratorAuthorizationManager<RequestAuthorizationContext> moderatorAccess = new ModeratorAuthorizationManager<>();
//        System.out.println(authenticationFilter.getFilterConfig());
//        authenticationProvider = new UserAuthenticationProvider(credentialsService);
//        SecurityContextHolder.getContext()
        http
                // disable csrf protection because it depends on frontend extra token
                .csrf((csrf) -> csrf.disable())
//                .authenticationProvider(authenticationProvider)
//                // attaches filter that extract credentials for login REST endpoint
//                .addFilterBefore(new UsernamePasswordAuthFilter(), BasicAuthenticationFilter.class)
//                // attaches filter that sets up AuthenticationToken by cookie
//                .addFilterBefore(new CookieAuthFilter(), UsernamePasswordAuthFilter.class)
//                .addFilterBefore(new CookieAuthFilter(authenticationManager), BasicAuthenticationFilter.class)
                .addFilter(authenticationFilter)
                // prevents Security from storing auth data for each authentication
                .sessionManagement((sessionManagement) ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
//                .rememberMe(Customizer.withDefaults()).userDetailsService(authenticationProvider.credentialsService)
//                .authenticationManager(new ProviderManager(authenticationProvider))
//                .authenticationProvider(authenticationProvider)
                // declare logout scenario that deletes auth cookie attached to "/auth/logout"
//                .logout((logout) ->
//                        logout.deleteCookies(CookieAuthFilter.AUTH_COOKIE_NAME)
//                                .logoutUrl("/api/auth/logout")
//                )
//                .authenticationManager(authenticationManager)
//                .authenticationProvider(authenticationProvider)
                // set matchers(rules by url patterns) that grants access
                .authorizeHttpRequests((authorizeHttpRequests) ->
                                authorizeHttpRequests

                                        // html pages BEGIN
                                        .requestMatchers("/forum/**").permitAll()
                                        .requestMatchers("/rules").authenticated()
                                        .requestMatchers("/feedback").authenticated()
                                        .requestMatchers("/styles/**").permitAll()
                                        .requestMatchers("/svg/**").permitAll()
                                        .requestMatchers("/login").permitAll()
                                        .requestMatchers("/signup").permitAll()
                                        .requestMatchers("/favicon.ico").permitAll()
                                        .requestMatchers("/u/*").authenticated()
                                        // html pages END
                                        // auth endpoints
                                        .requestMatchers("/rest/auth/login").permitAll()
                                        .requestMatchers("/rest/auth/logout").permitAll()
                                        .requestMatchers("/rest/auth/signup").permitAll()
                                        // usercard operations BEGIN
                                        .requestMatchers("/rest/list/usercard").hasAuthority("ADMIN")  // TODO test
                                        .requestMatchers(HttpMethod.GET, "/rest/usercard/*").authenticated()
                                        .requestMatchers(HttpMethod.PUT, "/rest/usercard/*").authenticated()
                                        .requestMatchers(HttpMethod.POST, "/rest/usercard/*").authenticated()
//                                        .requestMatchers(HttpMethod.PUT, "/rest/usercard/*").access(personalAccountAccess)  // TODO protect some fields
//                                .requestMatchers(HttpMethod.POST, "/rest/usercard/*").hasAuthority("ADMIN")
                                        .requestMatchers(HttpMethod.DELETE, "/rest/usercard/*").denyAll()
                                        // usercard operations END
                                        // section operations BEGIN
                                        .requestMatchers("/rest/list/section").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/rest/section/*").permitAll()
//                                .requestMatchers(HttpMethod.PUT, "/rest/section/*").hasAuthority("ADMIN")  // TODO enable
//                                .requestMatchers(HttpMethod.POST, "/rest/section/*").hasAuthority("ADMIN")
                                        .requestMatchers(HttpMethod.DELETE, "/rest/section/*").denyAll()
                                        // section operations END
                                        // topic operations BEGIN some check in controller
                                        .requestMatchers("/rest/list/topic").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/rest/topic/*").permitAll()
                                        .requestMatchers(HttpMethod.PUT, "/rest/topic/*").access(moderatorAccess)  // in controller
//                                .requestMatchers(HttpMethod.POST, "/rest/topic/*").authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/rest/topic/*").access(moderatorAccess)  // in controller
                                        // topic operations END
                                        // messages operations BEGIN
                                        .requestMatchers("/rest/list/message").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/rest/message/*").permitAll()
                                        .requestMatchers(HttpMethod.PUT, "/rest/message/*").hasAuthority("ADMIN")  // in controller
                                        .requestMatchers(HttpMethod.POST, "/rest/message/*").authenticated()  // will overwrite some fields
                                        .requestMatchers(HttpMethod.DELETE, "/rest/message/*").hasAuthority("ADMIN")  // in controller
                                        // messages operations END
                                        // all moders constraints checked in controllers
                                        // advanced admin endpoints
                                        .requestMatchers("/rest/section/*/moder/*").permitAll()
                                        .requestMatchers("/error").permitAll() // TODO hide
//                                        .requestMatchers("/section/*/moder").hasAuthority("ADMIN")
                                        .requestMatchers("/swagger-ui/**").permitAll()  // TODO hide under ADMIN
                                        .requestMatchers("/swagger-ui*").permitAll()  // TODO hide under ADMIN
                                        .requestMatchers("/v3/api-docs/**").permitAll()  // TODO hide under ADMIN

//                                        .requestMatchers("/**").permitAll()  // TODO
                )
//                .authenticationProvider(userAuthenticationProvider)
//                .userDetailsService(credentialsService)
//                .authenticationProvider(authenticationProvider)
                // declares unauthenticated redirect to login page
//                .authenticationProvider(authenticationProvider)
                .formLogin((formLogin) ->
                        formLogin.loginPage("/login")

                );

        return http.build();

    }


//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }


//    @Bean
//    public AuthenticationProvider authenticationProvider(CredentialsService credentialsService){
//        return new UserAuthenticationProvider(credentialsService);
//    }

    //    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http, CredentialsService credentialsService) throws Exception {
//        AuthenticationProvider myAuthenticationProvider = new UserAuthenticationProvider(credentialsService);
//        AuthenticationManagerBuilder authenticationManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.authenticationProvider(myAuthenticationProvider);
//        return authenticationManagerBuilder.build();
//    }
//    @Bean
//    public AuthenticationManager authenticationManagerBean() {
//        return new ProviderManager(authenticationProvider);
//    }
    @Bean
    SaltApplier saltApplier() {
        return new SimpleSaltApplier();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder builder) {
        builder.eraseCredentials(false);
    }

    @Bean
    AuthenticationConverter authenticationConverter() {
        return new AuthenticationConverter() {
            @Override
            public Authentication convert(HttpServletRequest request) {
                if (request.getCookies() == null)
                    return null;
                Optional<Cookie> cookieAuth = Arrays.stream(request.getCookies())
                        .filter(cookie -> Objects.equals(cookie.getName(), "auth-cookie")).findFirst();

                return cookieAuth.isEmpty() ? null : new PreAuthenticatedAuthenticationToken(
                        cookieAuth.get().getValue(),
                        null);
            }
        };
    }

    @Bean
    OncePerRequestFilter authenticationFilter(AuthenticationManager authenticationManager, AuthenticationConverter authenticationConverter) {
        BasicAuthenticationFilter basicAuthenticationFilter = new BasicAuthenticationFilter(authenticationManager);
        basicAuthenticationFilter.setAuthenticationConverter(authenticationConverter);
        basicAuthenticationFilter.afterPropertiesSet();
        return basicAuthenticationFilter;
    }

}
