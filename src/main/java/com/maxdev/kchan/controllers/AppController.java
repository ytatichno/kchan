package com.maxdev.kchan.controllers;

import com.maxdev.kchan.api.ForumApi;
import com.maxdev.kchan.exceptions.NonUniqueIdentifierException;
import com.maxdev.kchan.models.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;

/**
 * Created by ytati
 * on 05.03.2024.
 */
@RestController
@RequestMapping("/rest")
@Slf4j
@Tag(name = "AppController", description = "Kchan's rest controller")
public class AppController {

    @Value("${spring.datasource.driver-class-name}")
    private String dbmsDriverClassName;

    final ForumApi api;

    public AppController(ForumApi api) {
        this.api = api;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onAwake() {
        if (!dbmsDriverClassName.equals("org.postgresql.Driver")) {
            throw new IllegalStateException("Some queries are native for Postgres, " +
                    "but Postgres driver is not configured. " +
                    "Please set 'spring.datasource.driver-class-name=org.postgresql.Driver' " +
                    "in application.properties or provide JPQL implementation");
        }

    }

    @GetMapping("/list/usercard")
    ResponseEntity<?> getListOfUsercards(@RequestParam("page") Integer page,
                                         @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(api.getUsercards(page, pageSize));
    }

    @GetMapping("/usercard/{id}")
    ResponseEntity<?> getUsercard(@PathVariable(required = true) Integer id) {
        try {
            return ResponseEntity.ok(api.getUsercard(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/usercard/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    ResponseEntity<?> putUsercard(@PathVariable(required = false) Integer id,
                                  Usercard usercard,
                                  @AuthenticationPrincipal Credential user) {
        try {
            if (!user.getUsercard().getIsAdmin()) {
                usercard.setIsAdmin(false);
                usercard.setModerableSections(null);
                usercard.setMessages(null);
                usercard.setRegdate(null);
            }
            if (id != null)
                usercard.setId(id);
            int assignedId = api.upsertUsercard(usercard);
            return ResponseEntity.ok("upserted usercard with id:" + assignedId);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("can't find usercard with this id, if u want to insert new set id to -1");
        } catch (NonUniqueIdentifierException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("this nick is occupied, try another one");
        }
    }

    @DeleteMapping("/usercard/{id}")
    ResponseEntity<?> deleteUsercard(@PathVariable(required = true) Integer id) {
        try {
            return ResponseEntity.ok(api.deleteUsercard(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/list/section")
    ResponseEntity<?> getListOfSections(@RequestParam("page") Integer page,
                                        @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(api.getSections(page, pageSize));
    }

    @GetMapping("/section/{id}")
    ResponseEntity<?> getSection(@PathVariable(required = true) Integer id) {
        try {
            return ResponseEntity.ok(api.getSection(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/section/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    ResponseEntity<?> putSection(@PathVariable(required = false) Integer id, Section section) {
        try {
            if (id != null)
                section.setId(id);
            int assignedId = api.upsertSection(section);
            return ResponseEntity.ok("upserted section with id:" + assignedId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("can't find section with this id, if u want to insert new set id to -1");
        }
    }

    @DeleteMapping("/section/{id}")
    ResponseEntity<?> deleteSection(@PathVariable(required = true) Integer id) {
        try {
            return ResponseEntity.ok(api.deleteSection(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/list/topic")
    ResponseEntity<?> getListOfTopics(@RequestParam("section") Integer sectionId,
                                      @RequestParam("page") Integer page,
                                      @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(api.getTopics(sectionId, page, pageSize));
    }

    @GetMapping("/topic/{id}")
    ResponseEntity<?> getTopic(@PathVariable(required = true) Integer id) {
        try {
            return ResponseEntity.ok(api.getTopic(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/topic/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    ResponseEntity<?> putTopic(@PathVariable(required = false) Integer id, Topic topic) {
        try {
            if (id != null)
                topic.setId(id);
            int assignedId = api.upsertTopic(topic);
            return ResponseEntity.ok("upserted topic with id:" + assignedId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("can't find topic with this id, if u want to insert new set id to -1");
        }
    }

    @DeleteMapping("/topic/{id}")
    ResponseEntity<?> deleteTopic(@PathVariable(required = true) Integer id) {
        try {
            return ResponseEntity.ok(api.deleteTopic(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/list/message")
    ResponseEntity<?> getListOfMessages(@RequestParam("topic") Integer topicId,
                                        @RequestParam("page") Integer page,
                                        @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(api.getMessages(topicId, page, pageSize));
    }

    @GetMapping("/message/{uid}")
    ResponseEntity<?> getMessage(@PathVariable(required = true) Long uid) {
        try {
            return ResponseEntity.ok(api.getMessage(uid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/message/answer")
    ResponseEntity<?> postAnswerMessage(@RequestParam(required = true) Integer topic,
                                        @RequestBody Message message,
                                        @AuthenticationPrincipal Credential user){
        message.setUid(-1L);
        message.setAuthor(user.getUsercard());
        message.setCreated(Timestamp.from(Instant.now()));
        Topic minTopic = new Topic();
        minTopic.setId(topic);
        message.setTopic(minTopic);
        try {
            long assignedId = api.upsertMessage(message);
            return ResponseEntity.ok("posted answer with id:" + assignedId);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/message/{uid}", method = {RequestMethod.PUT, RequestMethod.POST})
    ResponseEntity<?> putMessage(@PathVariable(required = false) Long uid, @RequestBody Message message) {
        try {
            if (uid != null)
                message.setUid(uid);
            long assignedId = api.upsertMessage(message);
            return ResponseEntity.ok("upserted topic with id:" + assignedId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("can't find topic with this id, if u want to insert new set id to -1");
        }
    }

    @DeleteMapping("/message/{uid}")
    ResponseEntity<?> deleteMessage(@PathVariable(required = true) Long uid) {
        try {
            return ResponseEntity.ok(api.deleteMessage(uid));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/section/{id}/active")
    ResponseEntity<?> getActiveUsers(@PathVariable(required = true, name = "id") Integer sectionId,
                                     @RequestParam("page") Integer page,
                                     @RequestParam("pageSize") Integer pageSize) {
        try {
            return ResponseEntity.ok(api.getActiveUsercards(sectionId, page, pageSize));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/section/{id}/moders")
    ResponseEntity<?> getModersOfSection(@PathVariable(required = true, name = "id") Integer sectionId,
                                         @RequestParam("page") Integer page,
                                         @RequestParam("pageSize") Integer pageSize) {
        try {
            return ResponseEntity.ok(api.getModersOfSection(sectionId, page, pageSize));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/section/{sectionId}/moder/{usercardId}")
    ResponseEntity<?> assignModer(@PathVariable(required = true) Integer sectionId,
                                  @PathVariable(required = true) Integer usercardId) {
        try {
            api.assignModerOfSection(usercardId, sectionId);
            return ResponseEntity.ok("assign moder in section(" + sectionId + ") usercard(" + usercardId + ")");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/section/{sectionId}/moder/{usercardId}")
    ResponseEntity<?> disrankModer(@PathVariable(required = true) Integer sectionId,
                                   @PathVariable(required = true) Integer usercardId) {
        try {
            api.disrankModerOfSection(usercardId, sectionId);
            return ResponseEntity.ok("disrank moder in section(" + sectionId + ") usercard(" + usercardId + ")");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/topic/{topicId}/move/{sectionId}")
    ResponseEntity<?> moveTopic(@PathVariable(required = true) Integer topicId,
                                @PathVariable(required = true) Integer sectionId) {
        try {
            api.moveTopic(topicId, sectionId);
            return ResponseEntity.ok("topic(" + topicId + ") move to section(" + sectionId + ")");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PatchMapping("/message/{uid}/ban")
    ResponseEntity<?> banMessage(@PathVariable(required = true) Long uid,
                                 @RequestParam(required = false) String replacement){
        try {
            api.banMessage(uid, replacement);
            return ResponseEntity.ok("message(" + uid + ") ban");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
