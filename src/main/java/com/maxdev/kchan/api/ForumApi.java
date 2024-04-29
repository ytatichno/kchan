package com.maxdev.kchan.api;

import com.maxdev.kchan.exceptions.NonUniqueIdentifierException;
import com.maxdev.kchan.models.Message;
import com.maxdev.kchan.models.Section;
import com.maxdev.kchan.models.Topic;
import com.maxdev.kchan.models.Usercard;
import com.maxdev.kchan.models.enums.MessageStatus;
import com.maxdev.kchan.repo.MessagesRepository;
import com.maxdev.kchan.repo.SectionsRepository;
import com.maxdev.kchan.repo.TopicsRepository;
import com.maxdev.kchan.repo.UsercardsRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by ytati
 * on 05.03.2024.
 */
@Service
@Slf4j
public class ForumApi {

    @Autowired
    UsercardsRepository ur;
    @Autowired
    SectionsRepository sr;
    @Autowired
    TopicsRepository tr;
    @Autowired
    MessagesRepository mr;

    @Value("${kchan.enum.deleted-message-name")
    public String deletedMessageStatusName;
    private MessageStatus deletedMessageStatus;

    //// Usercard CRUD begin
    public List<Usercard> getUsercards(@Nullable Integer pageNumber,
                                       @Nullable Integer pageSize) {
        if (pageNumber == null)
            return ur.findAll();
        if (pageSize == null)
            pageSize = 10;

        return ur.findAll(PageRequest.of(pageNumber, pageSize)).toList();
    }

    public Long countUsercards() {
        return ur.count();
    }

    public Usercard getUsercard(@NotNull Integer id) {
        return ur.findById(id).orElseThrow();
    }

    public int upsertUsercard(@NotNull Usercard usercard)
            throws NonUniqueIdentifierException, ObjectNotFoundException {
        if (usercard.getId() == null || usercard.getId() < 0) {  // insert
            if (ur.findUsercardByNick(usercard.getNick()).isPresent()) {
                throw new NonUniqueIdentifierException(usercard.getNick(),
                        "Никнейм уже занят");
            }
            ur.save(usercard);
            log.debug("insert usercard with id" + usercard.getId());
        } else {  // update
            if (!ur.existsById(usercard.getId())) {
                throw new ObjectNotFoundException(usercard.getId(),
                        "Usercard");
            }
            ur.save(usercard);
            log.debug("update usercard with id" + usercard.getId());
        }
        return usercard.getId();
    }

    public Usercard deleteUsercard(@NotNull Integer id) {
        Usercard deleted = ur.findById(id).orElseThrow();
        ur.deleteById(id);
        return deleted;
    }

//// Usercard CRUD end

//// Credential CRUD begin
//// Credential CRUD end

    //// Section CRUD begin
    public List<Section> getSections(@Nullable Integer pageNumber,
                                     @Nullable Integer pageSize) {
        if (pageNumber == null)
            pageNumber = 0;
        if (pageSize == null)
            pageSize = 10;
        return sr.findAll(PageRequest.of(pageNumber, pageSize)).toList();

    }

    public Long countSections() {
        return sr.count();
    }

    public Section getSection(@NotNull Integer id) {
        return sr.findById(id).orElseThrow();
    }

    public int upsertSection(@NotNull Section section) {
        if (section.getId() == null || section.getId() < 0) {  // insert
            sr.save(section);
            log.debug("insert section with id" + section.getId());
        } else {  // update
            if (!sr.existsById(section.getId())) {
                throw new ObjectNotFoundException(section.getId(),
                        "Section");
            }
            sr.save(section);
            log.debug("update section with id" + section.getId());
        }
        return section.getId();
    }

    public Section deleteSection(@NotNull Integer id) {
        Section section = sr.findById(id).orElseThrow();
        sr.deleteById(id);
        return section;
    }
//// Section CRUD end

    //// Topic CRUD begin
    public List<Topic> getTopics(@Nullable Integer section,
                                 @Nullable Integer pageNumber,
                                 @Nullable Integer pageSize) {
        if (pageNumber == null)
            pageNumber = 0;
        if (pageSize == null)
            pageSize = 10;
        if (section == null)
            return tr.findAll(
                    PageRequest.of(pageNumber, pageSize)
            ).toList();

        return tr.findAllBySectionId(
                section,
                PageRequest.of(pageNumber, pageSize)
        ).toList();

    }

    public Long countTopics(@Nullable Integer section) {
        if (section == null)
            return tr.count();

        return tr.countAllBySectionId(section);
    }

    public Topic getTopic(@NotNull Integer id) {
        return tr.findById(id).orElseThrow();
    }

    public int upsertTopic(@NotNull Topic topic) {
        if (topic.getId() == null || topic.getId() < 0) {  // insert
            tr.save(topic);
            log.debug("insert topic with id" + topic.getId());
        } else {  // update
            if (!tr.existsById(topic.getId())) {
                throw new ObjectNotFoundException(topic.getId(),
                        "Topic");
            }
            tr.save(topic);
            log.debug("update topic with id" + topic.getId());
        }
        return topic.getId();
    }

    public Topic deleteTopic(@NotNull Integer id) {
        Topic topic = tr.findById(id).orElseThrow();
        tr.deleteById(id);
        return topic;
    }
//// Topic CRUD end

    //// Message CRUD begin
    public List<Message> getMessages(@Nullable Integer topic,
                                     @Nullable Integer pageNumber,
                                     @Nullable Integer pageSize) {
        if (pageNumber == null)
            pageNumber = 0;
        if (pageSize == null)
            pageSize = 20;
        if (topic == null)
            return mr.findAll(
                    PageRequest.of(pageNumber, pageSize)
            ).toList();
        return mr.findAllByTopicId(
                topic,
                PageRequest.of(pageNumber, pageSize)
        ).toList();
    }

    public Long countMessages(@NotNull Integer topic) {
        if (topic == null)
            return mr.count();

        return mr.countAllByTopicId(topic);
    }

    public Message getMessage(@NotNull Long id) {
        return mr.findById(id).orElseThrow();
    }

    public long upsertMessage(@NotNull Message message) {
        Message saved = null;
        if (message.getUid() == null || message.getUid() < 0) {  // insert
            saved = mr.save(message);
            log.debug("insert message with uid" + saved.getUid());
        } else {  // update
            if (!mr.existsById(message.getUid())) {
                throw new ObjectNotFoundException(message.getUid(),
                        "Message");
            }
            saved = mr.save(message);
            log.debug("update message with uid" + saved.getUid());
        }
        return saved.getUid();
    }

    public Message deleteMessage(@NotNull Long id) {
        Message message = mr.findById(id).orElseThrow();
        mr.deleteById(id);
        return message;
    }
//// Message CRUD end

    //// Section admins' operations begin
    public List<Pair<Usercard, Integer>> getActiveUsercards(
            @NotNull Integer section,
            @Nullable Integer pageNumber,
            @Nullable Integer pageSize
    ) {
        List<Map<String, Object>> tuples;
        if (pageNumber == null || pageSize == null)
            tuples = ur.findAllActiveUsersNative(section, 10, 0);
        else
            tuples = ur.findAllActiveUsersNative(section, pageSize,
                    pageSize * pageNumber);

        return tuples.stream()
                .map(
                        (tupleMap) -> Pair.of(
                                new Usercard(tupleMap),
                                Math.toIntExact((Long) tupleMap.get("activity"))
                        )
                )
                .toList();
    }

    public Long countActiveUsercards(@NotNull Integer section) {
        return ur.countAllActiveUsersNative(section);
    }

    public List<Usercard> getModersOfSection(
            @NotNull Integer section,
            @Nullable Integer pageNumber,
            @Nullable Integer pageSize
    ) {
        List<Usercard> moders;
        if (pageNumber == null || pageSize == null)
            moders = ur.findModersNative(section, 10, 0);
        else
            moders = ur.findModersNative(section, pageSize,
                    pageSize * pageNumber);

        return moders;
    }


    public void assignModerOfSection(@NotNull Integer usercard,
                                     @NotNull Integer section) {
        Usercard moder = ur.findById(usercard).orElseThrow();
        Section moderable = sr.findById(section).orElseThrow();
        moder.getModerableSections().add(moderable);
        Usercard uc = ur.save(moder);
    }

    public void disrankModerOfSection(@NotNull Integer usercard,
                                      @NotNull Integer section) {
        ur.disrankModer(section, usercard);
    }
//// Section admins' operations end

    //// global admins' operations begin


    public void moveTopic(@NotNull Integer topic,
                          @NotNull Integer destSection) {
        Topic toMove = tr.findById(topic).orElseThrow();
        Section section = sr.findById(destSection).orElseThrow();
        toMove.setSection(section);
        tr.save(toMove);
    }
//// global admins' operations begin

    public void banMessage(@NotNull Long id,
                           @Nullable String replacement) {

        Message message = mr.findById(id).orElseThrow();
        if (replacement != null)
            message.setMessage(replacement);
        message.setStatus(MessageStatus.BANNED);
        mr.save(message);
    }

    static class Utils {
        public static <T> Page<T> extractPageFromList(Pageable pageable, List<T> list, Predicate<? super T> filter) {
            List<T> filteredList = list.stream().filter(filter).toList();
            int start = Math.min((int) pageable.getOffset(), filteredList.size());
            int end = Math.min((int) pageable.getOffset() + pageable.getPageSize(), filteredList.size());
            List<T> content = filteredList.subList(start, end);
            return new PageImpl<>(content, pageable, content.size());
        }

        public static <T> Page<T> extractPageFromList(Pageable pageable, List<T> list) {
            return extractPageFromList(pageable, list, (a) -> true);
        }
    }

}
