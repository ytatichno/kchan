package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Message;
import com.maxdev.kchan.models.Section;
import com.maxdev.kchan.models.Topic;
import com.maxdev.kchan.models.Usercard;
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
public class MessagesRepositoryTest {
    @Autowired
    MessagesRepository messagesRepository;
    @Autowired
    UsercardsRepository usercardsRepository;
    @Autowired
    TopicsRepository topicsRepository;
    @Autowired
    SectionsRepository sectionsRepository;

    Usercard author;
    Topic topic;

    @BeforeEach
    public void init() {
        author = new Usercard();
        author.setNick("author");
        usercardsRepository.save(author);

        Section section = new Section();
        section.setName("section");
        sectionsRepository.save(section);
        topic = new Topic();
        topic.setName("topic");
        topic.setSection(section);
        topicsRepository.save(topic);
    }

    @Test
    public void MessagesRepository_Save_ReturnSavedMessage() {
        Message message = new Message();
        message.setMessage("message");
        message.setAuthor(author);
        message.setTopic(topic);

        Message savedMessage = messagesRepository.save(message);

        assertEquals(savedMessage, message);
    }

    @Test
    public void MessagesRepository_FindAll_ReturnAllMessages() {
        Message message1 = new Message();
        message1.setMessage("message1");
        message1.setAuthor(author);
        message1.setTopic(topic);
        messagesRepository.save(message1);
        Message message2 = new Message();
        message2.setMessage("message2");
        message2.setAuthor(author);
        message2.setTopic(topic);
        messagesRepository.save(message2);
        Message message3 = new Message();
        message3.setMessage("some message");
        message3.setAuthor(author);
        message3.setTopic(topic);
        messagesRepository.save(message3);

        List<Message> messageList = messagesRepository.findAll();

        assertEquals(messageList.size(), 3);
        assertAll("find same messages",
                () -> assertEquals(messageList.get(0), message1),
                () -> assertEquals(messageList.get(1), message2),
                () -> assertEquals(messageList.get(2), message3)
        );
    }

    @Test
    public void MessagesRepository_FindAllByTopic_ReturnAllMessagesFromTopic() {
        Topic anotherTopic = new Topic();
        anotherTopic.setSection(topic.getSection());
        topicsRepository.save(anotherTopic);

        Message message1 = new Message();
        message1.setMessage("message1");
        message1.setAuthor(author);
        message1.setTopic(topic);
        messagesRepository.save(message1);
        Message message2 = new Message();
        message2.setMessage("message2");
        message2.setAuthor(author);
        message2.setTopic(anotherTopic);  // another topic
        messagesRepository.save(message2);
        Message message3 = new Message();
        message3.setMessage("some message");
        message3.setAuthor(author);
        message3.setTopic(topic);
        messagesRepository.save(message3);

        List<Message> messageList = messagesRepository.findAllByTopicId(topic.getId(), null).toList();

        assertEquals(messageList.size(), 2);
        assertAll("find messages by topic",
                () -> assertEquals(messageList.get(0), message1),
                () -> assertEquals(messageList.get(1), message3)
        );
    }

    @Test
    public void MessagesRepository_FindAllByTopicAndPageable_ReturnAllMessagesFromTopicPaged() {
        Topic anotherTopic = new Topic();
        anotherTopic.setSection(topic.getSection());
        topicsRepository.save(anotherTopic);

        Message message1 = new Message();
        message1.setMessage("message1");
        message1.setAuthor(author);
        message1.setTopic(topic);
        messagesRepository.save(message1);
        Message message2 = new Message();
        message2.setMessage("message2");
        message2.setAuthor(author);
        message2.setTopic(anotherTopic);  // another topic
        messagesRepository.save(message2);
        Message message3 = new Message();
        message3.setMessage("some message");
        message3.setAuthor(author);
        message3.setTopic(topic);
        messagesRepository.save(message3);
        Message message4 = new Message();
        message4.setMessage("some more");
        message4.setAuthor(author);
        message4.setTopic(topic);
        messagesRepository.save(message4);

        List<Message> messageList = messagesRepository.findAllByTopicId(
                        topic.getId(),
                        PageRequest.of(0, 2))
                .toList();

        assertEquals(messageList.size(), 2);
        assertAll("find messages by topic and pageable",
                () -> assertEquals(messageList.get(0), message1),
                () -> assertEquals(messageList.get(1), message3)
        );
    }

    @Test
    public void MessagesRepository_FindById_ReturnMessageWithSameId() {
        Message message1 = new Message();
        message1.setMessage("message1");
        message1.setAuthor(author);
        message1.setTopic(topic);
        messagesRepository.save(message1);

        Optional<Message> savedMessage = messagesRepository.findById(message1.getUid());

        assertTrue(savedMessage.isPresent());
        assertEquals(message1, savedMessage.get());
    }

    @Test
    public void MessagesRepository_DeleteById_ReturnVoid() {
        Message message1 = new Message();
        message1.setMessage("message1");
        message1.setAuthor(author);
        message1.setTopic(topic);
        messagesRepository.save(message1);

        messagesRepository.deleteById(message1.getUid());
        Optional<Message> deletedMessage = messagesRepository.findById(message1.getUid());

        assertFalse(deletedMessage.isPresent());
    }
}
