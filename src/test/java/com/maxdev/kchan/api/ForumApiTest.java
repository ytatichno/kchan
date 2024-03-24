package com.maxdev.kchan.api;

import com.maxdev.kchan.exceptions.NonUniqueIdentifierException;
import com.maxdev.kchan.models.*;
import com.maxdev.kchan.repo.MessagesRepository;
import com.maxdev.kchan.repo.SectionsRepository;
import com.maxdev.kchan.repo.TopicsRepository;
import com.maxdev.kchan.repo.UsercardsRepository;
import org.hibernate.ObjectNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by ytati
 * on 09.03.2024.
 */
@ExtendWith(MockitoExtension.class)
class ForumApiTest {

    @Mock
    UsercardsRepository ur;
    @Mock
    SectionsRepository sr;
    @Mock
    TopicsRepository tr;
    @Mock
    MessagesRepository mr;

    @InjectMocks
    ForumApi api;

    @Nested
    class UsercardsCRUD {
        @Test
        void ForumApi_UpsertNewUsercard_ReturnAssignedId() {
            Usercard usercard = new Usercard();
            usercard.setNick("nick");
            when(ur.save(Mockito.any(Usercard.class))).then(
                    i -> {
                        Usercard arg = i.getArgument(0, Usercard.class);
                        arg.setId(1);
                        return arg;
                    });
            when(ur.findUsercardByNick(anyString())).thenReturn(Optional.empty());

            int id = api.upsertUsercard(usercard);

            assertTrue(id >= 1);
        }

        @Test
        void ForumApi_UpsertNewUsercardWithNickCollision_ThrowNonUniqueIdentifierException() {
            Usercard usercard = new Usercard();
            usercard.setNick("nick");
            when(ur.findUsercardByNick(anyString())).thenReturn(Optional.of(usercard));

            assertThrows(
                    NonUniqueIdentifierException.class,
                    () -> api.upsertUsercard(usercard),
                    "Expected NonUniqueIdentifierException when trying to insert usercard with non-unique name"
            );

        }

        @Test
        void ForumApi_UpsertUsercard_ReturnId() {
            Usercard usercard = new Usercard();
            usercard.setNick("nick");
            usercard.setId(42);
            when(ur.save(Mockito.any(Usercard.class))).then(returnsFirstArg());
            when(ur.existsById(anyInt())).thenReturn(true);

            int id = api.upsertUsercard(usercard);

            assertEquals(id, usercard.getId());
        }

        @Test
        void ForumApi_UpsertUsercardWithUnknownId_ThrowObjectNotFoundException() {
            Usercard usercard = new Usercard();
            usercard.setNick("nick");
            usercard.setId(42);
            when(ur.existsById(anyInt())).thenReturn(false);

            assertThrows(
                    ObjectNotFoundException.class,
                    () -> api.upsertUsercard(usercard),
                    "Expected ObjectNotFoundException when trying to update usercard with unknown id"
            );
        }

        @Test
        void ForumApi_GetUsercards_ReturnUsercards() {
            List<Usercard> usercards = new ArrayList<>();
            for (int i = 0; i < 15; ++i) {
                Usercard u = new Usercard();
                u.setId(i + 1);
                u.setNick("nick" + i);
                usercards.add(u);
            }

            when(ur.findAll()).thenReturn(usercards);
            when(ur.findAll(Mockito.any(Pageable.class))).then(
                    invocation -> {
                        Pageable p = invocation.getArgument(0, Pageable.class);
                        int end = Math.min((int) p.getOffset() + p.getPageSize(), usercards.size());
                        List<Usercard> content = usercards.subList((int) p.getOffset(), end);
                        return new PageImpl<>(content, p, content.size());
                    }
            );

            assertAll("get list of Usecrards failure",
                    () -> assertEquals(usercards, api.getUsercards(null, null)),
                    () -> assertEquals(usercards, api.getUsercards(null, 5)),
                    () -> assertEquals(usercards.subList(10, 15), api.getUsercards(1, null)),
                    () -> assertEquals(usercards.subList(6, 9), api.getUsercards(2, 3))
            );
        }

        @Test
        void ForumApi_GetUsercardById_ReturnUsercard() {
            Usercard usercard = new Usercard();
            usercard.setNick("nick");
            usercard.setId(42);

            when(ur.findById(Mockito.anyInt())).thenReturn(Optional.empty());
            when(ur.findById(42)).thenReturn(Optional.of(usercard));

            Usercard recievedUsercard = api.getUsercard(42);
            assertEquals(recievedUsercard, usercard);

            assertThrows(NoSuchElementException.class, () -> api.getUsercard(2024));

        }


        @Test
        void ForumApi_DeleteUsercardById_ReturnDeletedUsercard() {
            Usercard usercard = new Usercard();
            usercard.setNick("nick");
            usercard.setId(42);

            when(ur.findById(Mockito.anyInt())).thenReturn(Optional.empty());
            when(ur.findById(42)).thenReturn(Optional.of(usercard));

            Usercard recievedUsercard = api.deleteUsercard(42);
            assertEquals(recievedUsercard, usercard);

            assertThrows(NoSuchElementException.class, () -> api.deleteUsercard(2024));
        }
    }

    @Nested
    class SectionsCRUD {

        @Test
        void ForumApi_UpsertNewSection_ReturnAssignedId() {
            Section section = new Section();
            section.setName("section");
            when(sr.save(Mockito.any(Section.class))).then(
                    i -> {
                        Section arg = i.getArgument(0, Section.class);
                        arg.setId(1);
                        return arg;
                    });

            int id = api.upsertSection(section);

            assertTrue(id >= 1);
        }

        @Test
        void ForumApi_UpsertSection_ReturnId() {
            Section section = new Section();
            section.setName("name");
            section.setId(42);
            when(sr.save(Mockito.any(Section.class))).then(returnsFirstArg());
            when(sr.existsById(anyInt())).thenReturn(true);

            int id = api.upsertSection(section);

            assertEquals(id, section.getId());
        }

        @Test
        void ForumApi_UpsertSectionWithUnknownId_ThrowObjectNotFoundException() {
            Section section = new Section();
            section.setName("name");
            section.setId(42);
            when(sr.existsById(anyInt())).thenReturn(false);

            assertThrows(
                    ObjectNotFoundException.class,
                    () -> api.upsertSection(section),
                    "Expected ObjectNotFoundException when trying to update section with unknown id"
            );
        }

        @Test
        void ForumApi_GetSections_ReturnSections() {
            List<Section> sections = new ArrayList<>();
            for (int i = 0; i < 15; ++i) {
                Section s = new Section();
                s.setId(i + 1);
                s.setName("name" + i);
                sections.add(s);
            }

            when(sr.findAll(Mockito.any(Pageable.class))).then(
                    invocation -> {
                        Pageable p = invocation.getArgument(0, Pageable.class);
                        int end = Math.min((int) p.getOffset() + p.getPageSize(), sections.size());
                        List<Section> content = sections.subList((int) p.getOffset(), end);
                        return new PageImpl<>(content, p, content.size());
                    }
            );

            assertAll("get list of Sections failure",
                    () -> assertEquals(sections.subList(0, 10), api.getSections(null, null)),
                    () -> assertEquals(sections.subList(0, 5), api.getSections(null, 5)),
                    () -> assertEquals(sections.subList(10, 15), api.getSections(1, null)),
                    () -> assertEquals(sections.subList(6, 9), api.getSections(2, 3))
            );
        }

        @Test
        void ForumApi_GetSectionById_ReturnSection() {
            Section section = new Section();
            section.setName("name");
            section.setId(42);

            when(sr.findById(Mockito.anyInt())).thenReturn(Optional.empty());
            when(sr.findById(42)).thenReturn(Optional.of(section));

            Section recievedSection = api.getSection(42);
            assertEquals(recievedSection, section);

            assertThrows(NoSuchElementException.class, () -> api.getSection(2024));

        }


        @Test
        void ForumApi_DeleteSectionById_ReturnDeletedSection() {
            Section section = new Section();
            section.setName("name");
            section.setId(42);

            when(sr.findById(Mockito.anyInt())).thenReturn(Optional.empty());
            when(sr.findById(42)).thenReturn(Optional.of(section));

            Section recievedSection = api.deleteSection(42);
            assertEquals(recievedSection, section);

            assertThrows(NoSuchElementException.class, () -> api.deleteSection(2024));
        }
    }

    @Nested
    class TopicsCRUD {
        Section section;

        @BeforeEach
        public void init() {
            section = new Section();
            section.setName("secion");
            section.setId(32);
        }

        @Test
        void ForumApi_UpsertNewTopic_ReturnAssignedId() {
            Topic topic = new Topic();
            topic.setName("topic");
            when(tr.save(Mockito.any(Topic.class))).then(
                    i -> {
                        Topic arg = i.getArgument(0, Topic.class);
                        arg.setId(1);
                        return arg;
                    }
            );

            int id = api.upsertTopic(topic);

            assertTrue(id >= 1);
        }

        @Test
        void ForumApi_UpsertTopic_ReturnId() {
            Topic topic = new Topic();
            topic.setName("name");
            topic.setId(42);
            when(tr.save(Mockito.any(Topic.class))).then(returnsFirstArg());
            when(tr.existsById(anyInt())).thenReturn(true);

            int id = api.upsertTopic(topic);

            assertEquals(id, topic.getId());
        }

        @Test
        void ForumApi_UpsertTopicWithUnknownId_ThrowObjectNotFoundException() {
            Topic topic = new Topic();
            topic.setName("name");
            topic.setId(42);
            when(tr.existsById(anyInt())).thenReturn(false);

            assertThrows(
                    ObjectNotFoundException.class,
                    () -> api.upsertTopic(topic),
                    "Expected ObjectNotFoundException when trying to update topic with unknown id"
            );
        }


        @Test
        void ForumApi_GetTopics_ReturnTopics() {

            List<Topic> topics = new ArrayList<>();
            // Topic.section can't be Null in database
            Section otherSection = new Section();
            otherSection.setName("other");
            otherSection.setId(2024);

            for (int i = 0; i < 15; ++i) {
                Topic t = new Topic();
                t.setId(i + 1);
                t.setName("name" + i);
                if (i >= 5)
                    t.setSection(section);
                else
                    t.setSection(otherSection);
                topics.add(t);
            }


            when(tr.findAll(Mockito.any(Pageable.class))).then(
                    invocation -> ForumApi.Utils.extractPageFromList(
                            invocation.getArgument(0, Pageable.class),
                            topics
                    )
            );
            when(tr.findAllBySectionId(Mockito.anyInt(), Mockito.any(Pageable.class))).then(
                    invocation -> ForumApi.Utils.extractPageFromList(
                            invocation.getArgument(1, Pageable.class),
                            topics,
                            topic -> Objects.equals(topic.getSection().getId(), invocation.getArgument(0, Integer.class))
                    )
            );

            assertAll("get list of Topics failure",
                    () -> assertEquals(topics.subList(0, 10), api.getTopics(null, null, null)),
                    () -> assertEquals(topics.subList(0, 5), api.getTopics(null, null, 5)),
                    () -> assertEquals(topics.subList(10, 15), api.getTopics(null, 1, null)),
                    () -> assertEquals(topics.subList(11, 14), api.getTopics(section.getId(), 2, 3)),
                    () -> assertEquals(Collections.emptyList(), api.getTopics(5, 3, 3))
            );
        }

        @Test
        void ForumApi_GetTopicById_ReturnTopic() {
            Topic topic = new Topic();
            topic.setName("name");
            topic.setId(42);

            when(tr.findById(Mockito.anyInt())).thenReturn(Optional.empty());
            when(tr.findById(42)).thenReturn(Optional.of(topic));

            Topic recievedTopic = api.getTopic(42);
            assertEquals(recievedTopic, topic);

            assertThrows(NoSuchElementException.class, () -> api.getTopic(2024));

        }


        @Test
        void ForumApi_DeleteTopicById_ReturnDeletedTopic() {
            Topic topic = new Topic();
            topic.setName("name");
            topic.setId(42);

            when(tr.findById(Mockito.anyInt())).thenReturn(Optional.empty());
            when(tr.findById(42)).thenReturn(Optional.of(topic));

            Topic recievedTopic = api.deleteTopic(42);
            assertEquals(recievedTopic, topic);

            assertThrows(NoSuchElementException.class, () -> api.deleteTopic(2024));
        }
    }

    @Nested
    class MessagesCRUD {

        Section section;
        Topic topic;

        @BeforeEach
        public void init() {
            section = new Section();
            section.setName("secion");
            section.setId(32);
            topic = new Topic();
            topic.setName("topic");
            topic.setId(32);
            topic.setSection(section);
        }

        @Test
        void ForumApi_UpsertNewMessage_ReturnAssignedUid() {
            Message message = new Message();
            message.setMessage("new message");
            when(mr.save(Mockito.any(Message.class))).then(
                    i -> {
                        Message arg = i.getArgument(0, Message.class);
                        arg.setUid(1L);
                        return arg;
                    }
            );

            long id = api.upsertMessage(message);

            assertTrue(id >= 1L);
        }

        @Test
        void ForumApi_UpsertMessage_ReturnUid() {
            Message message = new Message();
            message.setMessage("message");
            message.setUid(42L);
            when(mr.save(Mockito.any(Message.class))).then(returnsFirstArg());
            when(mr.existsById(anyLong())).thenReturn(true);

            long id = api.upsertMessage(message);

            assertEquals(id, message.getUid());
        }

        @Test
        void ForumApi_UpsertMessageWithUnknownUid_ThrowObjectNotFoundException() {
            Message message = new Message();
            message.setMessage("message");
            message.setUid(42L);
            when(mr.existsById(anyLong())).thenReturn(false);

            assertThrows(
                    ObjectNotFoundException.class,
                    () -> api.upsertMessage(message),
                    "Expected ObjectNotFoundException when trying to update message with unknown uid"
            );
        }


        @Test
        void ForumApi_GetMessages_ReturnMessages() {

            List<Message> messages = new ArrayList<>();
            // Message.topic can't be Null in database
            Topic otherTopic = new Topic();
            otherTopic.setName("other topic name");
            otherTopic.setId(2024);

            for (int i = 0; i < 15; ++i) {
                Message m = new Message();
                m.setUid(i + 1L);
                m.setMessage("message " + i);
                if (i >= 5)
                    m.setTopic(topic);
                else
                    m.setTopic(otherTopic);
                messages.add(m);
            }


            when(mr.findAll(Mockito.any(Pageable.class))).then(
                    invocation -> ForumApi.Utils.extractPageFromList(
                            invocation.getArgument(0, Pageable.class),
                            messages
                    )
            );
            when(mr.findAllByTopicId(Mockito.anyInt(), Mockito.any(Pageable.class))).then(
                    invocation -> ForumApi.Utils.extractPageFromList(
                            invocation.getArgument(1, Pageable.class),
                            messages,
                            message -> Objects.equals(message.getTopic().getId(), invocation.getArgument(0, Integer.class))
                    )
            );

            assertAll("get list of Topics failure",
                    () -> assertEquals(messages.subList(0, Math.min(messages.size(), 20)), api.getMessages(null, null, null)),
                    () -> assertEquals(messages.subList(0, 5), api.getMessages(null, null, 5)),
                    () -> assertEquals(Collections.emptyList(), api.getMessages(null, 1, null)),
                    () -> assertEquals(messages.subList(11, 14), api.getMessages(topic.getId(), 2, 3)),
                    () -> assertEquals(Collections.emptyList(), api.getMessages(5, 3, 3))
            );
        }

        @Test
        void ForumApi_GetMessageByUid_ReturnMessage() {
            Message message = new Message();
            message.setMessage("message");
            message.setUid(42L);

            when(mr.findById(Mockito.anyLong())).thenReturn(Optional.empty());
            when(mr.findById(42L)).thenReturn(Optional.of(message));

            Message recievedMessage = api.getMessage(42L);
            assertEquals(recievedMessage, message);

            assertThrows(NoSuchElementException.class, () -> api.getMessage(2024L));

        }


        @Test
        void ForumApi_DeleteMessageByUid_ReturnDeletedMessage() {
            Message message = new Message();
            message.setMessage("message");
            message.setUid(42L);

            when(mr.findById(Mockito.anyLong())).thenReturn(Optional.empty());
            when(mr.findById(42L)).thenReturn(Optional.of(message));

            Message deletedMessage = api.deleteMessage(42L);
            assertEquals(deletedMessage, message);

            assertThrows(NoSuchElementException.class, () -> api.deleteMessage(2024L));
        }
    }

    @Nested
    class AdvancedOperations {

//        private <E, S> List<Pair<E, S>> stackListsInPairs(
//                @NotNull List<E> entityList,
//                @NotNull List<S> statList) {
//            if (entityList.size() != statList.size())
//                throw new IllegalArgumentException("Lists aren't have same size");
//            List<Pair<E, S>> result = new ArrayList<>(entityList.size());
//            for (int i = 0; i < entityList.size(); ++i) {
//                result.add(Pair.of(entityList.get(i), statList.get(i)));
//            }
//            return result;
//        }

        private <E, S> List<Map<String, Object>> stackListsInMappedTuple(
                @NotNull List<E> entityList,
                @NotNull List<S> statList) {
            if (entityList.size() != statList.size())
                throw new IllegalArgumentException("Lists aren't have same size");
            List<Map<String, Object>> result = new ArrayList<>(entityList.size());
            for (int i = 0; i < entityList.size(); ++i) {
//                result.add(Pair.of(entityList.get(i), statList.get(i)));
                if (!(entityList.get(0) instanceof Usercard))
                    throw new IllegalArgumentException("entityList containees doesn't have toMap()");
                Map<String, Object> map = ((Usercard) entityList.get(i)).toMap();
                map.put("activity", statList.get(i));
                result.add(map);
            }
            return result;
        }

        Function<Map<String, Object>, Pair<Usercard, Integer>> findActiveUsersMapper =
                (tupleMap) -> Pair.of(
                        new Usercard(tupleMap),
                        Math.toIntExact((Long) tupleMap.get("activity"))
                );

        @Test
        void ForumApi_GetActiveUsercards_ReturnPairsUsercardAndMessagesCount() {
            // prepare users and sorted random sequence
            List<Usercard> usercards = new ArrayList<>();
            List<Long> sequence = new ArrayList<>();
            Random random = new Random();
            long maxRandom = 1000000;
            for (int i = 0; i < 15; ++i) {
                Usercard u = new Usercard();
                u.setId(i + 1);
                u.setNick("nick" + i);
                usercards.add(u);
                sequence.add(maxRandom = random.nextLong((maxRandom) + 1));
            }


            when(ur.findAllActiveUsersNative(anyInt(), anyInt(), anyInt())).then(
                    invocation -> {
                        int pageSize = invocation.getArgument(1, Integer.class);
                        int pageNumber = invocation.getArgument(2, Integer.class) / pageSize;
                        Pageable p = PageRequest.of(
                                pageNumber,
                                pageSize
                        );
                        List<Usercard> pagedUsercards = ForumApi.Utils.extractPageFromList(p, usercards).toList();
                        List<Long> pagedSequenence = ForumApi.Utils.extractPageFromList(p, sequence).toList();

                        return stackListsInMappedTuple(pagedUsercards, pagedSequenence);
//                        .stream()
//                                .map(findActiveUsersMapper)
//                                .toList();
                    }
            );

            assertAll("get list of active users of section failed",
                    () -> assertEquals(
                            stackListsInMappedTuple(usercards.subList(6, 9), sequence.subList(6, 9)).stream()
                                    .map(findActiveUsersMapper)
                                    .toList(),
                            api.getActiveUsercards(32, 2, 3)
                    ),
                    () -> assertEquals(
                            stackListsInMappedTuple(usercards.subList(0, 10), sequence.subList(0, 10)).stream()
                                    .map(findActiveUsersMapper)
                                    .toList(),
                            api.getActiveUsercards(32, null, null)
                    ),
                    () -> assertEquals(
                            stackListsInMappedTuple(usercards.subList(0, 10), sequence.subList(0, 10)).stream()
                                    .map(findActiveUsersMapper)
                                    .toList(),
                            api.getActiveUsercards(32, null, 3)
                    ),
                    () -> assertEquals(
                            stackListsInMappedTuple(usercards.subList(0, 10), sequence.subList(0, 10)).stream()
                                    .map(findActiveUsersMapper)
                                    .toList(),
                            api.getActiveUsercards(32, 1, null)
                    )
            );
        }

        @Test
        void ForumApi_GetModersOfSection_ReturnModersOfSpecifiedSection() {
            // maybe I should use more deterministic asserts in case of use of LinkedHashSet
            Section section = new Section();
            section.setName("section");
            section.setModers(new LinkedHashSet<>(10));
            section.setId(42);

            Section otherSection = new Section();
            otherSection.setName("other");
            otherSection.setModers(new LinkedHashSet<>(10));
            otherSection.setId(2024);

            Section withoutModers = new Section();
            withoutModers.setName("I have no moders");
            withoutModers.setId(1613);

            LinkedHashSet<Usercard> usercards = new LinkedHashSet<>();
            for (int i = 0; i < 15; ++i) {
                Usercard u = new Usercard();
                u.setId(i + 1);
                u.setNick("nick" + i);
                usercards.add(u);
                if (i >= 5)
                    section.getModers().add(u);
                else
                    otherSection.getModers().add(u);
            }


            when(sr.findById(anyInt())).thenReturn(Optional.empty());
            when(sr.findById(section.getId())).thenReturn(Optional.of(section));
            when(sr.findById(otherSection.getId())).thenReturn(Optional.of(otherSection));
            when(sr.findById(withoutModers.getId())).thenReturn(Optional.of(withoutModers));


            assertAll("get list of moders of section failure",
                    () -> assertEquals(usercards.stream().toList().subList(0, 5),
                            api.getModersOfSection(otherSection.getId(), null, 5)
                    ),
                    () -> assertEquals(usercards.stream().toList().subList(5, 15),
                            api.getModersOfSection(section.getId(), 0, null)
                    ),
                    () -> assertEquals(usercards.stream().toList().subList(11, 14),
                            api.getModersOfSection(section.getId(), 2, 3)
                    ),
                    () -> assertEquals(Collections.emptyList(),
                            api.getModersOfSection(5, 3, 3)
                    ),
                    () -> assertEquals(Collections.emptyList(),
                            api.getModersOfSection(withoutModers.getId(), 3, 3)
                    )
            );

        }

        @Test
        void ForumApi_AssignModerOfSection_ReturnVoid() {
            Usercard moder = new Usercard();
            moder.setNick("BestModer");
            moder.setId(32);

            Usercard newModer = new Usercard();
            newModer.setNick("NewModer");
            newModer.setId(42);

            Set<Usercard> moders = new LinkedHashSet<>();
            moders.add(moder);

            Section section = new Section();
            section.setName("HasModer");
            section.setId(2024);
            section.setModers(moders);

            when(ur.findById(anyInt())).thenReturn(Optional.empty());
            when(ur.findById(moder.getId())).thenReturn(Optional.of(moder));
            when(ur.findById(newModer.getId())).thenReturn(Optional.of(newModer));

            when(sr.findById(anyInt())).thenReturn(Optional.empty());
            when(sr.findById(section.getId())).thenReturn(Optional.of(section));

//            when(sr.save(Mockito.any(Section.class))).thenAnswer(returnsFirstArg());

            assertAll("assign Usecard as moder to Section",
                    () -> assertThrows(
                            IllegalArgumentException.class,
                            () -> api.assignModerOfSection(null, section.getId())
                    ),
                    () -> assertThrows(
                            IllegalArgumentException.class,
                            () -> api.assignModerOfSection(null, -5)
                    ),
                    () -> assertThrows(
                            NoSuchElementException.class,
                            () -> api.assignModerOfSection(newModer.getId(), -5)
                    ),
                    () -> assertThrows(
                            IllegalArgumentException.class,
                            () -> api.assignModerOfSection(moder.getId(), null)
                    ),
                    () -> assertThrows(
                            IllegalArgumentException.class,
                            () -> api.assignModerOfSection(-5, null)
                    ),
                    () -> assertThrows(
                            NoSuchElementException.class,
                            () -> api.assignModerOfSection(-5, section.getId())
                    ),
                    () -> {
                        Set<Usercard> expected = new LinkedHashSet<>();
                        expected.add(moder);
                        api.assignModerOfSection(moder.getId(), section.getId());
                        assertEquals(expected, section.getModers());
                    },
                    () -> {
                        Set<Usercard> expected = new LinkedHashSet<>();
                        expected.add(moder);
                        expected.add(newModer);
                        api.assignModerOfSection(newModer.getId(), section.getId());
                        assertEquals(expected, section.getModers());
                    }
            );


        }

        @Test
        void ForumApi_DisrankModerOfSection_ReturnVoid() {
            Usercard moder = new Usercard();
            moder.setNick("BestModer");
            moder.setId(32);

            Usercard newModer = new Usercard();
            newModer.setNick("NewModer");
            newModer.setId(42);

            Set<Usercard> moders = new LinkedHashSet<>();
            moders.add(moder);
            moders.add(newModer);

            Section section = new Section();
            section.setName("HasModer");
            section.setId(2024);
            section.setModers(moders);

            when(ur.findById(anyInt())).thenReturn(Optional.empty());
            when(ur.findById(moder.getId())).thenReturn(Optional.of(moder));
            when(ur.findById(newModer.getId())).thenReturn(Optional.of(newModer));

            when(sr.findById(anyInt())).thenReturn(Optional.empty());
            when(sr.findById(section.getId())).thenReturn(Optional.of(section));

//            when(sr.save(Mockito.any(Section.class))).thenAnswer(returnsFirstArg());

            assertAll("assign Usecard as moder to Section",
                    () -> assertThrows(
                            IllegalArgumentException.class,
                            () -> api.disrankModerOfSection(null, section.getId())
                    ),
                    () -> assertThrows(
                            IllegalArgumentException.class,
                            () -> api.disrankModerOfSection(null, -5)
                    ),
                    () -> assertThrows(
                            NoSuchElementException.class,
                            () -> api.disrankModerOfSection(newModer.getId(), -5)
                    ),
                    () -> assertThrows(
                            IllegalArgumentException.class,
                            () -> api.disrankModerOfSection(moder.getId(), null)
                    ),
                    () -> assertThrows(
                            IllegalArgumentException.class,
                            () -> api.disrankModerOfSection(-5, null)
                    ),
                    () -> assertThrows(
                            NoSuchElementException.class,
                            () -> api.disrankModerOfSection(-5, section.getId())
                    ),
                    () -> {
                        Set<Usercard> expected = new LinkedHashSet<>();
                        expected.add(newModer);
                        api.disrankModerOfSection(moder.getId(), section.getId());
                        assertEquals(expected, section.getModers());
                    },
                    () -> {
                        Set<Usercard> expected = Collections.emptySet();
                        api.disrankModerOfSection(newModer.getId(), section.getId());
                        assertEquals(expected, section.getModers());
                    },
                    () -> assertThrows(
                            ArrayStoreException.class,
                            () -> api.disrankModerOfSection(moder.getId(), section.getId())
                    )
            );
        }

        @Test
        void ForumApi_MoveTopic_ReturnVoid() {
            Section srcSection = new Section();
            srcSection.setName("src");
            srcSection.setId(32);

            Section dstSection = new Section();
            dstSection.setName("dst");
            dstSection.setId(42);

            Topic toMoveTopic = new Topic();
            toMoveTopic.setName("to move");
            toMoveTopic.setSection(srcSection);
            toMoveTopic.setId(2024);

            when(tr.findById(anyInt())).thenReturn(Optional.empty());
            when(tr.findById(toMoveTopic.getId())).thenReturn(Optional.of(toMoveTopic));
            when(sr.findById(anyInt())).thenReturn(Optional.empty());
            when(sr.findById(dstSection.getId())).thenReturn(Optional.of(dstSection));

            assertEquals(toMoveTopic.getSection(), srcSection);

            api.moveTopic(toMoveTopic.getId(), dstSection.getId());
            assertAll("move topic to another section",
                    () -> assertThrows(NoSuchElementException.class,
                            () -> api.moveTopic(-5, dstSection.getId())
                    ),
                    () -> assertThrows(NoSuchElementException.class,
                            () -> api.moveTopic(toMoveTopic.getId(), -5)
                    ),
                    () -> assertThrows(NoSuchElementException.class,
                            () -> api.moveTopic(-5, -5)
                    ),
                    () -> assertEquals(toMoveTopic.getSection(), dstSection)
            );

        }

        @Test
        void ForumApi_BanMessage_ReturnVoid() {

            Message toBan = new Message();
            toBan.setTopic(new Topic());
            toBan.setAuthor(new Usercard());
            toBan.setMessage("Fuck you!");
            toBan.setUid(42L);

            when(mr.findById(anyLong())).thenReturn(Optional.empty());
            when(mr.findById(42L)).thenReturn(Optional.of(toBan));

            assertAll("ban inappropriate message",
                    () -> assertThrows(
                            NoSuchElementException.class,
                            () -> api.banMessage(-5L, null)
                    ),
                    () -> {
                        api.banMessage(toBan.getUid(), "** banned: too rude **");
                        assertEquals(toBan.getMessage(), "** banned: too rude **");
                        assertEquals(toBan.getStatus(), MessageStatus.BANNED);
                    }
            );
        }
    }
}