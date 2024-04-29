package com.maxdev.kchan.controllers;

import com.maxdev.kchan.api.ForumApi;
import com.maxdev.kchan.models.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

/**
 * Created by ytati
 * on 03.03.2024.
 */
@Controller
@Slf4j
public class MainController {

    @Autowired
    ForumApi api;

    @EventListener(ApplicationReadyEvent.class)
    public void onAwake() {
//        System.out.println("Main controller init");
        log.debug("Main controller init");

//        authManager.init();
    }

    @GetMapping("/forum")
    public String home(Model model,
                       @RequestParam(name = "p", required = false, defaultValue = "0") int pageNumber,
                       @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                       @AuthenticationPrincipal Credential user) {
        log.debug("at home");
//        log.error(session.toString());
        model.addAttribute("title", "main page");
//        log.debug(authentication.getAuthorities().toString());
//        model.addAttribute("user", extractEmail(session));
        model.addAttribute("user", user);
        List<Section> sections = api.getSections(pageNumber, pageSize);
        model.addAttribute("sections", sections);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", api.countSections() / pageSize + 1);
        model.addAttribute("url", "forum");
        return "home";
    }

    @GetMapping("/forum/{section_id}")
    public String section(Model model,
                          @PathVariable(required = true) int section_id,
                          @RequestParam(name = "p", required = false, defaultValue = "0") Integer pageNumber,
                          @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                          @AuthenticationPrincipal Credential user
            /*@RequestParam("upd") Boolean upd*/) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Section section = api.getSection(section_id);
        log.debug("at section: " + section.getName());
        log.debug("moders: " + section.getModers());
        model.addAttribute("title", section.getName());
//        model.addAttribute("user", extractEmail((String) auth.getPrincipal()));
        model.addAttribute("user", user);
        model.addAttribute("section", section);
        List<Topic> topics = api.getTopics(section_id, pageNumber, pageSize);
        model.addAttribute("topics", topics);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", api.countTopics(section_id) / pageSize + 1);
        model.addAttribute("url", "forum/" + section_id);
//        model.addAttribute("canEdit", isAdmin(auth));  //
        model.addAttribute("canEdit", user != null && user.getUsercard().getIsAdmin());
//        model.addAttribute("canEdit", true);

        return "section";
    }

    @GetMapping("/forum/{section_id}/users")
    public String section_users(Model model,
                                @PathVariable(required = true) int section_id,
                                @RequestParam(name = "p", required = false, defaultValue = "0") Integer pageNumber,
                                @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                @AuthenticationPrincipal Credential user
    ) throws IllegalAccessException {
//        if(!c.getUsercard().getIsAdmin())  //TODO NotNull
//            throw new IllegalAccessException("Unauthorized");
        Section section = api.getSection(section_id);
        model.addAttribute("showModers", false);
        model.addAttribute("title", "Активные пользователи раздела " + section.getName());
//        model.addAttribute("user", extractEmail(session));
        model.addAttribute("user", user);
        model.addAttribute("section", section);
        List<Pair<Usercard, Integer>> activeUsers = api.getActiveUsercards(section_id, pageNumber, pageSize);
        log.warn(api.countActiveUsercards(section_id).toString());
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", api.countActiveUsercards(section_id) / pageSize + 1);  // TODO check
        model.addAttribute("url", "forum/" + section_id + "/users");
        return "section_users";
    }

    @GetMapping("/forum/{section_id}/moders")
    public String section_moders(Model model,
                                 @PathVariable(required = true) int section_id,
                                 @RequestParam(name = "p", required = false, defaultValue = "0") Integer pageNumber,
                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                 @AuthenticationPrincipal Credential user) throws IllegalAccessException {
//        if(!c.getUsercard().getIsAdmin())  // TODO NotNull
//            throw new IllegalAccessException("Unauthorized");
        Section section = api.getSection(section_id);
        model.addAttribute("showModers", true);
        model.addAttribute("title", "Модераторы раздела " + section.getName());
        model.addAttribute("user", user);
        model.addAttribute("section", section);
        List<Usercard> moders = api.getModersOfSection(section_id, pageNumber, pageSize);
        model.addAttribute("moders", moders);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", section.countModers() / pageSize + 1);
        model.addAttribute("url", "forum/" + section_id + "/moders");
        return "section_users";
    }

    @GetMapping("/forum/{section_id}/{topic_id}")
    public String topic(Model model,
                        @PathVariable(required = true) int section_id,
                        @PathVariable(required = true) int topic_id,
                        @RequestParam(name = "p", required = false, defaultValue = "0") Integer pageNumber,
                        @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                        @AuthenticationPrincipal Credential user
            /*@RequestParam("upd") Boolean upd*/) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Section section = api.getSection(section_id);
        Topic topic = api.getTopic(topic_id);
        log.debug("at topic: " + topic.getName());
        model.addAttribute("title", topic.getName());
        model.addAttribute("user", user);
        model.addAttribute("topic", topic);
        model.addAttribute("section", section);
        List<Message> messages = api.getMessages(topic_id, pageNumber, pageSize);
        model.addAttribute("messages", messages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", api.countMessages(topic_id) / pageSize + 1);
        model.addAttribute("url", "forum/" + section_id + "/" + topic_id);
        final Integer userId = user != null && user.getUsercard() != null ? user.getUsercard().getId() : null;
        boolean canEdit = userId != null && topic.getAuthor() != null
                && (
                user.getUsercard().getIsAdmin()
                        || Objects.equals(userId, topic.getAuthor().getId())
                        || topic.getSection().getModers().stream().anyMatch(u -> Objects.equals(u.getId(), userId))
        );
        model.addAttribute("canEdit", canEdit);
//        model.addAttribute("canEdit", true);

        return "topic";
    }

    @GetMapping("/feedback")
    public String feedback(Model model, @AuthenticationPrincipal Credential user) {
        model.addAttribute("title", "feedback");
        model.addAttribute("user", user);

        return "feedback";
    }

    @GetMapping("/rules")
    public String rules(Model model, @AuthenticationPrincipal Credential user) {
        model.addAttribute("title", "rules");
        model.addAttribute("user", user);

        return "rules";
    }

    @GetMapping("/u/{id}")
    public String usercard(Model model, @PathVariable Integer id, @AuthenticationPrincipal Credential user) {

        log.debug("usercard:  " + id);
        Usercard usercard = api.getUsercard(id);
//        Usercard usercard = api.getUsercard(id);
        log.warn("moderable: " + usercard.getModerableSections());
        model.addAttribute("title", usercard.getNick());
//        model.addAttribute("user", extractEmail((String) auth.getPrincipal()));
        model.addAttribute("user", user);

//        model.addAttribute("id", usercard.getId());
//        model.addAttribute("nick", usercard.getNick());
//        model.addAttribute("about", usercard.getAbout());
//        model.addAttribute("birthday", usercard.getBirthday());
//        model.addAttribute("regdate", usercard.getRegdate());
        boolean canEdit = user.getUsercard().getIsAdmin() || id.equals(user.getUsercard().getId());
//        model.addAttribute("canEdit", user.getUsercard(). || Objects.equals(extractId((String) auth.getPrincipal()), id));
        model.addAttribute("canEdit", canEdit);  // TODO admin or owner
        model.addAttribute("isAdminWatching", user.getUsercard().getIsAdmin());
//        model.addAttribute("isOwner", true);
        return "usercard";
    }

    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(name = "status", required = false) String status,
                        @AuthenticationPrincipal Credential user) {
        if (user != null)
            return "forward:/forum";
        if (status != null && !status.isEmpty())
            model.addAttribute("status", status);
        model.addAttribute("title", "Авторизация");
//        model.addAttribute("user", session.split("&")[1]);
        model.addAttribute("signup", false);

        return "auth";
    }

    @GetMapping("/signup")
    public String signup(Model model, @AuthenticationPrincipal Credential user) {
        if (user != null)
            return "forward:/forum";

        model.addAttribute("title", "Регистрация");
//        model.addAttribute("user", session.split("&")[1]);
        model.addAttribute("signup", true);
        return "auth";
    }

    @Nullable
    @Deprecated
    private String extractEmail(String sessionCookie) {
        if (sessionCookie == null) return null;
        if (sessionCookie.contains("&"))  // it's separator every sessionCookie has them except unauthenticated
            return sessionCookie.split("&", 3)[1];

        return null;
    }

    @Nullable
    @Deprecated
    private Integer extractId(String sessionCookie) {
        if (sessionCookie == null) return null;
        if (sessionCookie.contains("&"))  // it's separator every sessionCookie has them except unauthenticated
            return Integer.parseInt(sessionCookie.split("&", 2)[0]);

        return null;
    }

    @Deprecated
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
    }
}
