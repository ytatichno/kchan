package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Credential;
import com.maxdev.kchan.models.Usercard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by ytati
 * on 24.03.2024.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CredentialsRepositoryTest {
    @Autowired
    CredentialsRepository credentialsRepository;
    @Autowired
    UsercardsRepository usercardsRepository;

    @Test
    public void CredentialsRepository_Save_ReturnSavedCredential() {
        Usercard usercard = new Usercard();
        usercard.setNick("Nick");
        usercardsRepository.save(usercard);

        Credential credential = new Credential();
        credential.setEmail("liam@mail.ru");
        credential.setPwd("s|o/m?e!H@s$h89");
        credential.setUsercard(usercard);

        Credential savedCredential = credentialsRepository.save(credential);

        assertEquals(savedCredential, credential);
    }

    @Test
    public void CredentialsRepository_ExistsByEmail_ReturnTrueIfCredentialWithSameEmailExists() {
        Usercard usercard = new Usercard();
        usercard.setNick("Nick");
        usercardsRepository.save(usercard);

        Credential credential = new Credential();
        credential.setEmail("liam@mail.ru");
        credential.setPwd("s|o/m?e!H@s$h89");
        credential.setUsercard(usercard);

        assertFalse(credentialsRepository.existsByEmail(credential.getEmail()));

        credentialsRepository.save(credential);

        assertTrue(credentialsRepository.existsByEmail(credential.getEmail()));
    }

    @Test
    public void CredentialsRepository_FindCredentialByEmail_ReturnCredentialWithSameEmail() {
        Usercard usercard = new Usercard();
        usercard.setNick("Nick");
        usercardsRepository.save(usercard);

        Credential credential = new Credential();
        credential.setEmail("liam@mail.ru");
        credential.setPwd("s|o/m?e!H@s$h89");
        credential.setUsercard(usercard);

        assertFalse(credentialsRepository.findCredentialByEmail(credential.getEmail()).isPresent());

        credentialsRepository.save(credential);

        assertEquals(credentialsRepository.findCredentialByEmail(credential.getEmail()).orElseThrow(), credential);
    }
}
