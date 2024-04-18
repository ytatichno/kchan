package com.maxdev.kchan.security;

import com.maxdev.kchan.security.filters.CookieAuthFilter;
import com.maxdev.kchan.security.filters.UsernamePasswordAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

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

//    @Autowired
//    public SecurityConfig(CredentialsService userDetailsServiceImpl) {
//        this.userDetailsServiceImpl = userDetailsServiceImpl;
//    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        PersonalAccountAuthorizationManager<RequestAuthorizationContext> personalAccountAccess = new PersonalAccountAuthorizationManager<>();
        ModeratorAuthorizationManager<RequestAuthorizationContext> moderatorAccess = new ModeratorAuthorizationManager<>();
        http
                // disable csrf protection because it depends on frontend extra token
                .csrf((csrf) -> csrf.disable())
                // attaches filter that extract credentials for login REST endpoint
                .addFilterBefore(new UsernamePasswordAuthFilter(), BasicAuthenticationFilter.class)
                // attaches filter that sets up AuthenticationToken by cookie
                .addFilterBefore(new CookieAuthFilter(), UsernamePasswordAuthFilter.class)
                // prevents Security from storing auth data for each authentication
                .sessionManagement((sessionManagement) ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // declare logout scenario that deletes auth cookie attached to "/auth/logout"
                .logout((logout) ->
                        logout.deleteCookies(CookieAuthFilter.AUTH_COOKIE_NAME)
                                .logoutUrl("/api/auth/logout")
                )
                // set matchers(rules by url patterns) that grants access
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                            // html pages BEGIN
                                .requestMatchers("/forum/**").permitAll()
                                .requestMatchers("/rules").permitAll()
                                .requestMatchers("/feedback").authenticated()
                                .requestMatchers("/styles/**").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/signup").permitAll()
                            // html pages END
                            // auth endpoints
                                .requestMatchers("/rest/auth/**").permitAll()
                            // usercard operations BEGIN
                                .requestMatchers("/rest/list/usercard").hasAuthority("ADMIN")  // TODO test
                                .requestMatchers(HttpMethod.GET, "/rest/usercard/*").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/rest/usercard/*").access(personalAccountAccess)  // TODO protect some fields
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
                            .requestMatchers("/swagger-ui/**").permitAll()  // TODO hide under ADMIN
                            .requestMatchers("/**").permitAll()  // TODO
                )
                // declares unauthenticated redirect to login page
                .formLogin((formLogin) ->
                        formLogin.loginPage("/login")

                );

        return http.build();

    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
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

    @Bean
    SaltApplier saltApplier() {
        return new SimpleSaltApplier();
    }

}