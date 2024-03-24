package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Credential;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by ytati
 * on 05.03.2024.
 */
public interface CredentialsRepository extends JpaRepository<Credential, Integer> {
    Optional<Credential> findCredentialByEmail(@Email String email);

    boolean existsByEmail(@Email String email);
}
