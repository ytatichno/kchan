package com.maxdev.kchan.security;

import com.maxdev.kchan.models.Credential;
import com.maxdev.kchan.repo.CredentialsRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
import java.util.Collections;
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
    private final SessionFactory sessionFactory;

    @Autowired
    public CredentialsService(CredentialsRepository cr, PasswordEncoder passwordEncoder, SaltApplier saltApplier, SessionFactory sessionFactory) {
        this.cr = cr;
        this.passwordEncoder = passwordEncoder;
        this.saltApplier = saltApplier;
        this.sessionFactory = sessionFactory;
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

    public Credential findByToken(String token) throws RuntimeException {
        String[] parts = token.split("&");

        int userId = Integer.parseInt(parts[0]);
        String email = parts[1];
        String hmac = parts[2];

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Optional<Credential> response = cr.findCredentialByEmail(email);
        if (response.isEmpty())
            return null;
        Credential credential = response.get();
//        System.out.println(credential.getUsercard().getModerableSections());
//        Usercard awaitedUsercard = session.get(Usercard.class, credential.getUsercard().getId());
//        credential.setUsercard(awaitedUsercard);
//        System.out.println("------ before lazy check");
//        System.out.println("lazy??" + credential.getUsercard().getModerableSections());
//        System.out.println("------ after lazy check");
        session.detach(credential.getUsercard());
        credential.getUsercard().setModerableSections(Collections.emptySet());
        session.getTransaction().commit();
        session.close();
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
