package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Credential;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ytati
 * on 05.03.2024.
 */
public interface CredentialsRepository extends JpaRepository<Credential, Integer> {
    Credential findCredentialByEmail(@Email String email);
}
