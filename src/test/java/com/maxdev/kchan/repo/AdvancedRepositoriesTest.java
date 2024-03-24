package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Message;
import com.maxdev.kchan.models.Section;
import com.maxdev.kchan.models.Topic;
import com.maxdev.kchan.models.Usercard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by ytati
 * on 24.03.2024.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AdvancedRepositoriesTest {
    @Autowired
    UsercardsRepository usercardsRepository;
    @Autowired
    SectionsRepository sectionsRepository;
    @Autowired
    TopicsRepository topicsRepository;
    @Autowired
    MessagesRepository messagesRepository;

    @Test
    public void UsercardsRepository_FindAllActiveUsersNative_ReturnListOfUsercardsWithActivityStatSortedByActivityStatDesc() {
        Section section = new Section();
        section.setName("section");
        sectionsRepository.save(section);

        Section unusedSection = new Section();
        unusedSection.setName("section to test that query selects by section");
        sectionsRepository.save(unusedSection);

        Topic topic = new Topic();
        topic.setName("topic");
        topic.setSection(section);
        topicsRepository.save(topic);

        Topic unusedTopic = new Topic();
        unusedTopic.setName("topic to test join topics");
        unusedTopic.setSection(section);
        topicsRepository.save(unusedTopic);

        Usercard user0 = new Usercard();
        user0.setNick("user0");
        user0.setMessages(835);  // count of all messages by this user, mostly more than in selected section
        usercardsRepository.save(user0);

        Usercard user1 = new Usercard();
        user1.setNick("user1");
        user1.setMessages(8435);  // count of all messages by this user, mostly more than in selected section
        usercardsRepository.save(user1);

        Usercard user2 = new Usercard();
        user2.setNick("user2");
        user2.setMessages(256);  // count of all messages by this user, mostly more than in selected section
        usercardsRepository.save(user2);

        Random randomizer = new Random();
        List<Message> messages = new ArrayList<Message>();
        for (int i = 0; i < 40; i++) {
            Message m = new Message();
            m.setMessage("doesn't matter");
            m.setAuthor(List.of(user0, user1, user2).get(randomizer.nextInt(3)));

            if (randomizer.nextInt(4) >= 1)
                m.setTopic(topic);
            else
                m.setTopic(unusedTopic);

            messagesRepository.save(m);
            messages.add(m);
        }

        assertAll("advanced native method find all active users of section",
                // check throws with limit = null
                () -> assertThrows(
                        DataIntegrityViolationException.class,
                        () -> usercardsRepository.findAllActiveUsersNative(section.getId(), null, 3)
                ),
                // check throws with offset = null
                () -> assertThrows(
                        DataIntegrityViolationException.class,
                        () -> usercardsRepository.findAllActiveUsersNative(section.getId(), 5, null)
                ),
                // check throws with limit = null and offset = null
                () -> assertThrows(
                        DataIntegrityViolationException.class,
                        () -> usercardsRepository.findAllActiveUsersNative(section.getId(), null, null)
                ),
                // check with section = null
                () -> assertThrows(
                        DataIntegrityViolationException.class,
                        () -> usercardsRepository.findAllActiveUsersNative(section.getId(), null, null)
                ),
                () -> {
                    List<Pair<Usercard, Integer>> result =
                            usercardsRepository.findAllActiveUsersNative(section.getId(), 5, 0).stream()
                                    .map(
                                            (tupleMap) -> Pair.of(
                                                    new Usercard(tupleMap),
                                                    Math.toIntExact((Long) tupleMap.get("activity"))
                                            )
                                    )
                                    .toList();
                    // assert already sorted by activity desc
                    assertEquals(
                            result,
                            result.stream()
                                    .sorted(
                                            (pairX, pairY) -> -1 * pairX.getSecond().compareTo(pairY.getSecond())
                                    )
                                    .toList()
                    );
                    List<Pair<Usercard, Integer>> expected = new ArrayList<>();
                    for (Usercard u : List.of(user0, user1, user2)) {
                        expected.add(
                                Pair.of(
                                        u,
                                        Math.toIntExact(messages.stream()
                                                .filter((m) -> m.getAuthor().equals(u))
                                                .count())
                                )
                        );
                    }
                    expected = expected.stream()
                            .sorted(
                                    (pairX, pairY) -> -1 * pairX.getSecond().compareTo(pairY.getSecond())
                            )
                            .toList();
                    assertEquals(result, expected);

                }

        );

    }
}
