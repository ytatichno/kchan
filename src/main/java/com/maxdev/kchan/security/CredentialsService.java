package com.maxdev.kchan.security;

import com.maxdev.kchan.models.Credential;
import com.maxdev.kchan.repo.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Created by ytati
 * on 22.03.2024.
 */
@Service
public class CredentialsService implements UserDetailsService {


    private final CredentialsRepository cr;
    private final PasswordEncoder passwordEncoder;
    private final SaltApplier saltApplier;

    @Autowired
    public CredentialsService(CredentialsRepository cr, PasswordEncoder passwordEncoder, SaltApplier saltApplier) {
        this.cr = cr;
        this.passwordEncoder = passwordEncoder;
        this.saltApplier = saltApplier;
    }

    public Credential authenticate(String email, String password) {
        Optional<Credential> response = cr.findCredentialByEmail(email);
        if (response.isEmpty())
            return null;
        Credential credential = response.get();
        if (!validatePassword(password, credential))
            return null;

        return credential;
    }

    public Credential findByToken(String token) {
        String[] parts = token.split("&");

        int userId = Integer.parseInt(parts[0]);
        String email = parts[1];
        String hmac = parts[2];

        Optional<Credential> response = cr.findCredentialByEmail(email);
        if (response.isEmpty())
            return null;
        Credential credential = response.get();

        if (!hmac.equals(calculateHmac(credential)) || userId != credential.getId()) {
            throw new RuntimeException("Invalid Cookie value");
        }

        return credential;
    }

    public String createToken(Credential user) {
        return user.getId() + "&" + user.getEmail() + "&" + calculateHmac(user);
    }

    private String calculateHmac(Credential user) {
        return passwordEncoder.encode(user.getId() + "&" + user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Credential userCredential = cr.findCredentialByEmail(email).orElseThrow();

        return new User(userCredential.getEmail(), userCredential.getPwd(), getUserAuthorities(userCredential));
    }

    public Collection<GrantedAuthority> getUserAuthorities(Credential user) {
        Collection<GrantedAuthority> userAuthorities = new ArrayList<>(1);
        if (user.getUsercard().getIsAdmin())
            userAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
        return userAuthorities;
    }

    private boolean validatePassword(String password, Credential credential) {
        return passwordEncoder.matches(
                saltApplier.applySalt(password, credential.getSalt(), credential.getSaltmode()),
                credential.getPwd()
        );

    }


}
