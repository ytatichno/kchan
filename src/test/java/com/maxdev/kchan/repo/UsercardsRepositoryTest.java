package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Usercard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by ytati
 * on 09.03.2024.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UsercardsRepositoryTest {
    @Autowired
    UsercardsRepository usercardsRepository;


    @Test
    public void UsercardsRepoitory_Save_ReturnSavedUsercard() {
        Usercard usercard1 = new Usercard();
        usercard1.setNick("nick1");
        Usercard savedUsercard = usercardsRepository.save(usercard1);

        assertEquals(savedUsercard, usercard1);

    }

    @Test
    public void UsercardsRepoitory_FindAll_ReturnAllUsercards() {
        Usercard usercard1 = new Usercard();
        usercard1.setNick("nick1");
        usercardsRepository.save(usercard1);
        Usercard usercard2 = new Usercard();
        usercard2.setNick("nick2");
        usercardsRepository.save(usercard2);

        List<Usercard> usercardList = usercardsRepository.findAll();

        assertEquals(usercardList.size(), 2);
        assertEquals(usercardList.get(0).getNick(), "nick1");
        assertEquals(usercardList.get(1).getNick(), "nick2");

    }

    @Test
    public void UsercardsRepoitory_FindAllByPageable_ReturnAllUsercards() {
        Usercard usercard1 = new Usercard();
        usercard1.setNick("nick1");
        usercardsRepository.save(usercard1);
        Usercard usercard2 = new Usercard();
        usercard2.setNick("nick2");
        usercardsRepository.save(usercard2);
        Usercard usercard3 = new Usercard();
        usercard3.setNick("nick3");
        usercardsRepository.save(usercard3);

        List<Usercard> usercardList = usercardsRepository.findAll(PageRequest.of(0, 2)).toList();

        assertEquals(usercardList.size(), 2);
        assertEquals(usercardList.get(0).getNick(), "nick1");
        assertEquals(usercardList.get(1).getNick(), "nick2");

    }

    @Test
    public void UsercardsRepoitory_FindById_ReturnUsercardWithSameId() {
        Usercard usercard1 = new Usercard();
        usercard1.setNick("nick1");
        usercardsRepository.save(usercard1);

        Optional<Usercard> savedUsercard = usercardsRepository.findById(usercard1.getId());

        assertTrue(savedUsercard.isPresent());
        assertEquals(savedUsercard.get().getId(), usercard1.getId());

    }

    @Test
    public void UsercardsRepository_FindUsercardByNick_ReturnUsercardWithSameNick() {
        // Arrange
        Usercard usercard = new Usercard();
        usercard.setNick("nick");
        usercard.setAbout("I am ...");
        usercardsRepository.save(usercard);

        // Act
        Optional<Usercard> savedUsercard = usercardsRepository.findUsercardByNick("nick");

        //Assert
        assertTrue(savedUsercard.isPresent());
        assertAll("Assert correct fields",
                () -> assertEquals(savedUsercard.get().getNick(), "nick"),
                () -> assertEquals(savedUsercard.get().getAbout(), "I am ..."));
    }

    @Test
    public void UsercardsRepoitory_DeleteById_ReturnVoid() {
        Usercard usercard1 = new Usercard();
        usercard1.setNick("nick1");
        usercardsRepository.save(usercard1);

        usercardsRepository.deleteById(usercard1.getId());

        Optional<Usercard> savedUsercard = usercardsRepository.findById(usercard1.getId());
        assertFalse(savedUsercard.isPresent());
    }


}
