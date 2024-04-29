package com.maxdev.kchan.controllers;

import com.maxdev.kchan.models.Credential;
import com.maxdev.kchan.models.Usercard;
import com.maxdev.kchan.repo.CredentialsRepository;
import com.maxdev.kchan.repo.UsercardsRepository;
import com.maxdev.kchan.security.CredentialsService;
import com.maxdev.kchan.security.SaltApplier;
import com.maxdev.kchan.security.SimpleSaltApplier;
import com.maxdev.kchan.security.filters.CookieAuthFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by ytati
 * on 23.03.2024.
 */
@RestController
@RequestMapping("/rest/auth")
@Slf4j
public class AuthController {
    //    private AuthenticationManager authenticationManager;
    private UsercardsRepository ur;
    private CredentialsRepository cr;
    private PasswordEncoder passwordEncoder;
    private SaltApplier saltApplier;
    private CredentialsService credentialsService;
    private AuthenticationManager authenticationManager;

    @Value("${security.jwt.token.expiration-seconds}")
    private int COOKIE_TTL;

    @Autowired
    public AuthController(UsercardsRepository ur, CredentialsRepository cr, PasswordEncoder passwordEncoder, SaltApplier saltApplier, CredentialsService credentialsService, AuthenticationManager authenticationManager) {
        this.ur = ur;
        this.cr = cr;
        this.passwordEncoder = passwordEncoder;
        this.saltApplier = saltApplier;
        this.credentialsService = credentialsService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(Credential user,
                                   @RequestParam(value = "httpResponse", required = false, defaultValue = "false") boolean httpResponse,
                                   HttpServletResponse servletResponse) {

        try {
            UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                    user.getEmail(), user.getPwd());
            Authentication authentication = authenticationManager.authenticate(token);  // throws
            Cookie authCookie = getAuthCookie((Credential) authentication.getPrincipal());
//            Cookie authCookie = getAuthCookie((Credential) authentication.getCredentials());
            servletResponse.addCookie(authCookie);

            if(httpResponse){
                servletResponse.addHeader(HttpHeaders.LOCATION, "/forum");
                return ResponseEntity.status(HttpStatus.FOUND).build();
            }
            return ResponseEntity.ok("Successfully logged in");

        } catch (AuthenticationException e) {
            if(httpResponse){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("")
                servletResponse.addHeader(HttpHeaders.LOCATION, "/login?status=wrong");
                return ResponseEntity.status(HttpStatus.FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials");
        }
//        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
//        context.setAuthentication(authentication);
//        securityContextHolderStrategy.setContext(context);
//        securityContextRepository.saveContext(context, request, response);

//        SecurityContextHolder.getContext().setAuthentication(authentication);



//        if(user == null){
//            if(httpResponse){
////                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("")
//                servletResponse.addHeader(HttpHeaders.LOCATION, "/login?status=wrong");
//                return ResponseEntity.status(HttpStatus.FOUND).build();
//            }
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials");
//        }

//        Cookie authCookie = getAuthCookie(user);
//
//        servletResponse.addCookie(authCookie);
//
//        return ResponseEntity.ok("Successfully logged in");
    }


    @RequestMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal String user,
                                    @RequestParam(value = "httpResponse", required = false, defaultValue = "false") boolean httpResponse,
                                    HttpServletResponse servletResponse) {
//        log.warn("user logout: " + user.getUsercard().getNick());
        SecurityContextHolder.clearContext();

        Cookie expiredCookie = new Cookie(CookieAuthFilter.AUTH_COOKIE_NAME, "");
        expiredCookie.setHttpOnly(true);
        expiredCookie.setSecure(true);
        expiredCookie.setPath("/");
        expiredCookie.setMaxAge(0);  // now expired and ruined
        servletResponse.addCookie(expiredCookie);

        if(httpResponse)
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/login").build();

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestParam HashMap<String, String> body,
                                    @RequestParam(value = "httpResponse", required = false, defaultValue = "false") boolean httpResponse,
                                    HttpServletResponse servletResponse) {

        String email = (String) body.get("email");
        String nick = (String) body.get("nickname");
        String pwd = (String) body.get("pwd");

//        log.warn("user signup: " + email + ", " + nick);

        if (cr.existsByEmail(email))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already taken");
        if (ur.existsByNick(nick))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nickname already taken");
        if (!checkPasswordRobustness(pwd))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Password too weak, try another");

        // register usercard
        Usercard usercard = new Usercard();
        usercard.setNick(nick);
        ur.save(usercard);

        // register credential
        Credential credential = new Credential();
        credential.setEmail(email);
        credential.setSalt("" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        credential.setPwd(
                passwordEncoder.encode(
                        saltApplier.applySalt(
                                pwd,
                                credential.getSalt(),
                                SimpleSaltApplier.SimpleSaltMode.DUPLICATE_TO_BOTH_ENDS.ordinal()
                        )
                )
        );
        // link usercatd to credential
        credential.setUsercard(usercard);

        try {
            cr.save(credential);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }

        Cookie authCookie = getAuthCookie(credential);
        servletResponse.addCookie(authCookie);

        if(httpResponse){
            servletResponse.addHeader(HttpHeaders.LOCATION, "/login");
            return ResponseEntity.status(HttpStatus.FOUND).build();
        }

        return ResponseEntity.ok("Successfully signed up");
    }

    @NotNull
    private Cookie getAuthCookie(Credential user) {
        Cookie authCookie = new Cookie(CookieAuthFilter.AUTH_COOKIE_NAME, credentialsService.createToken(user));
        authCookie.setHttpOnly(true);
        authCookie.setSecure(true);
        authCookie.setMaxAge((int) Duration.of(COOKIE_TTL, ChronoUnit.SECONDS).toSeconds());
        authCookie.setPath("/");
        return authCookie;
    }

    private boolean checkPasswordRobustness(String pwd) {
        return pwd.length() >= 4;
    }

}
