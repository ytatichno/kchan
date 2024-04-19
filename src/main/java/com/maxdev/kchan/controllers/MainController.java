package com.maxdev.kchan.controllers;

import com.maxdev.kchan.api.ForumApi;
import com.maxdev.kchan.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Pair;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.spring6.view.ThymeleafView;

import java.util.List;

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
                       @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        log.debug("at home");
        model.addAttribute("title", "main page");
        List<Section> sections = api.getSections(pageNumber, pageSize);
        model.addAttribute("sections", sections);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", api.countSections()/pageSize + 1);
        model.addAttribute("url", "forum");
        return "home";
    }

    @GetMapping("/forum/{section_id}")
    public String section(Model model,
                          @PathVariable(required = true) int section_id,
                          @RequestParam(name = "p", required = false, defaultValue = "0") Integer pageNumber,
                          @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                          @AuthenticationPrincipal Credential c
                          /*@RequestParam("upd") Boolean upd*/) {
        Section section = api.getSection(section_id);
        log.debug("at section: " + section.getName());
        model.addAttribute("title", section.getName());
        model.addAttribute("section", section);
        List<Topic> topics = api.getTopics(section_id, pageNumber, pageSize);
        model.addAttribute("topics", topics);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", api.countTopics(section_id)/pageSize + 1);
        model.addAttribute("url", "forum/" + section_id);
//        model.addAttribute("canEdit", c.getUsercard().getIsAdmin());  // TODO
        model.addAttribute("canEdit", true);

        return "section";
    }

    @GetMapping("/forum/{section_id}/users")
    public String section_users(Model model,
                                @PathVariable(required = true) int section_id,
                                @RequestParam(name = "p", required = false, defaultValue = "0") Integer pageNumber,
                                @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                @AuthenticationPrincipal Credential c) throws IllegalAccessException {
//        if(!c.getUsercard().getIsAdmin())  //TODO NotNull
//            throw new IllegalAccessException("Unauthorized");
        model.addAttribute("showModers", false);
        model.addAttribute("title", "Активные пользователи раздела " + api.getSection(section_id).getName());
        List<Pair<Usercard,Integer>> activeUsers = api.getActiveUsercards(section_id, pageNumber, pageSize);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", 10);  // TODO
        model.addAttribute("url", "forum/" + section_id + "/users");
        return "section_users";
    }
    @GetMapping("/forum/{section_id}/moders")
    public String section_moders(Model model,
                                 @PathVariable(required = true) int section_id,
                                 @RequestParam(name = "p", required = false, defaultValue = "0") Integer pageNumber,
                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                 @AuthenticationPrincipal Credential c) throws IllegalAccessException {
//        if(!c.getUsercard().getIsAdmin())  // TODO NotNull
//            throw new IllegalAccessException("Unauthorized");
        model.addAttribute("showModers", true);
        model.addAttribute("title", "Модераторы раздела " + api.getSection(section_id).getName());
        List<Usercard> activeUsers = api.getModersOfSection(section_id, pageNumber, pageSize);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", 10);  // TODO
        model.addAttribute("url", "forum/" + section_id + "/moders");
        return "section_users";
    }

    @GetMapping("/forum/{section_id}/{topic_id}")
    public String topic(Model model,
                        @PathVariable(required = true) int section_id,
                        @PathVariable(required = true) int topic_id,
                        @RequestParam(name = "p", required = false, defaultValue = "0") Integer pageNumber,
                        @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                        @AuthenticationPrincipal Credential c
            /*@RequestParam("upd") Boolean upd*/) {
        Section section = api.getSection(section_id);
        Topic topic = api.getTopic(topic_id);
        log.debug("at topic: " + topic.getName());
        model.addAttribute("title", topic.getName());
        model.addAttribute("topic", topic);
        model.addAttribute("section", section);
        List<Message> messages = api.getMessages(topic_id, pageNumber, pageSize);
        model.addAttribute("messages", messages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", api.countMessages(topic_id)/pageSize + 1);
        model.addAttribute("url", "forum/" + section_id + "/" + topic_id);
//        model.addAttribute("canEdit", c.getUsercard().getIsAdmin());  // TODO
        model.addAttribute("canEdit", true);

        return "topic";
    }

    @GetMapping("/feedback")
    public String feedback(Model model) {
        model.addAttribute("title", "feedback");
        return "feedback";
    }

    @GetMapping("/rules")
    public String rules(Model model) {
        model.addAttribute("title", "rules");
        return "rules";
    }

    @GetMapping("/u/{id}")
    public String usercard(Model model, @PathVariable Integer id) {
        log.debug("usercard:  " + id);
        Usercard usercard = api.getUsercard(id);
        model.addAttribute("nick", usercard.getNick());
        model.addAttribute("about", usercard.getAbout());
        model.addAttribute("birthday", usercard.getBirthday());
        model.addAttribute("regdate", usercard.getRegdate());
        model.addAttribute("canEdit", true);  // TODO admin or owner

//        model.addAttribute("isOwner", true);
        return "usercard";
    }

    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(name = "status", required = false) String status,
                        @AuthenticationPrincipal Credential user) {
        if(user != null)
            return "forward:/forum";
        if(status != null && !status.isEmpty())
            model.addAttribute("status", status);
        model.addAttribute("title", "Авторизация");
        model.addAttribute("signup", false);

        return "auth";
    }

    @GetMapping("/signup")
    public String signup(Model model, @AuthenticationPrincipal Credential user) {
        if(user != null)
            return "forward:/forum";

        model.addAttribute("title", "Регистрация");
        model.addAttribute("signup", true);
        return "auth";
    }
}
