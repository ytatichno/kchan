package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Section;
import com.maxdev.kchan.models.Topic;
import org.junit.jupiter.api.BeforeEach;
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
 * on 10.03.2024.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TopicsRepositoryTest {
    @Autowired
    TopicsRepository topicsRepository;
    @Autowired
    SectionsRepository sectionsRepository;
    Section section;

    @BeforeEach
    public void init() {
        section = new Section();
        section.setName("section");
        sectionsRepository.save(section);
    }

    @Test
    public void TopicsRepository_Save_ReturnSavedTopic() {
        Topic topic = new Topic();
        topic.setName("topic1");
        topic.setDescription("descr");
        topic.setSection(section);

        Topic savedTopic = topicsRepository.save(topic);

        assertEquals(savedTopic, topic);
    }

    @Test
    public void TopicsRepository_FindAll_ReturnAllTopics() {
        Topic topic1 = new Topic();
        topic1.setName("topic1");
        topic1.setDescription("descr1");
        topic1.setSection(section);
        topicsRepository.save(topic1);
        Topic topic2 = new Topic();
        topic2.setName("topic2");
        topic2.setDescription("descr2");
        topic2.setSection(section);
        topicsRepository.save(topic2);

        List<Topic> topicList = topicsRepository.findAll();

        assertEquals(topicList.size(), 2);
        assertAll("find same sections",
                () -> assertEquals(topicList.get(0), topic1),
                () -> assertEquals(topicList.get(1), topic2)
        );
    }

    @Test
    public void TopicsRepository_FindAllByPageable_ReturnAllTopicsPaged() {
        Topic topic1 = new Topic();
        topic1.setName("topic1");
        topic1.setDescription("descr1");
        topic1.setSection(section);
        topicsRepository.save(topic1);
        Topic topic2 = new Topic();
        topic2.setName("topic2");
        topic2.setDescription("descr2");
        topic2.setSection(section);
        topicsRepository.save(topic2);

        List<Topic> topicList = topicsRepository.findAll(PageRequest.of(1, 1)).toList();

        assertEquals(topicList.size(), 1);
        assertEquals(topicList.get(0), topic2);
    }

    @Test
    public void TopicsRepository_FindAllBySection_ReturnAllTopicsFromSections() {
        Topic topic1 = new Topic();
        topic1.setName("topic1");
        topic1.setDescription("descr1");
        topic1.setSection(section);
        topicsRepository.save(topic1);
        Topic topic2 = new Topic();
        topic2.setName("topic2");
        topic2.setDescription("descr2");
        topic2.setSection(section);
        topicsRepository.save(topic2);

        List<Topic> topicList = topicsRepository.findAllBySectionId(section.getId(), null).toList();

        assertEquals(topicList.size(), 2);
        assertAll("find same sections",
                () -> assertEquals(topicList.get(0), topic1),
                () -> assertEquals(topicList.get(1), topic2)
        );
    }

    @Test
    public void TopicsRepository_FindAllBySectionAndPageable_ReturnAllTopicsFromSectionsPaged() {
        Topic topic1 = new Topic();
        topic1.setName("topic1");
        topic1.setDescription("descr1");
        topic1.setSection(section);
        topicsRepository.save(topic1);
        Topic topic2 = new Topic();
        topic2.setName("topic2");
        topic2.setDescription("descr2");
        topic2.setSection(section);
        topicsRepository.save(topic2);
        Topic topic3 = new Topic();
        topic3.setName("topic3");
        topic3.setDescription("descr3");
        topic3.setSection(section);
        topicsRepository.save(topic3);

        List<Topic> topicList = topicsRepository.findAllBySectionId(
                        section.getId(),
                        PageRequest.of(0, 2))
                .toList();

        assertEquals(topicList.size(), 2);
        assertAll("find same sections",
                () -> assertEquals(topicList.get(0), topic1),
                () -> assertEquals(topicList.get(1), topic2)
        );
    }

    @Test
    public void TopicsRepository_FindById_ReturnTopicWithSameId() {
        Topic topic1 = new Topic();
        topic1.setName("topic1");
        topic1.setDescription("descr1");
        topic1.setSection(section);
        topicsRepository.save(topic1);

        Optional<Topic> savedTopic = topicsRepository.findById(topic1.getId());

        assertTrue(savedTopic.isPresent());
        assertEquals(savedTopic.get(), topic1);
    }

    @Test
    public void TopicsRepository_DeleteById_ReturnVoid() {
        Topic topic1 = new Topic();
        topic1.setName("topic1");
        topic1.setDescription("descr1");
        topic1.setSection(section);
        topicsRepository.save(topic1);

        topicsRepository.deleteById(topic1.getId());

        Optional<Topic> deletedTopic = topicsRepository.findById(topic1.getId());
        assertFalse(deletedTopic.isPresent());
    }
}
