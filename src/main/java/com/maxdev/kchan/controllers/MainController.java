package com.maxdev.kchan.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by ytati
 * on 03.03.2024.
 */
@Controller
@Slf4j
public class MainController {

    @EventListener(ApplicationReadyEvent.class)
    public void onAwake() {
//        System.out.println("Main controller init");
        log.debug("Main controller init");

//        authManager.init();
    }

    @GetMapping("/forum")
    public String home(Model model) {
        model.addAttribute("title", "main page");
        log.debug("at home");
        return "home";
    }

    @GetMapping("/forum/{section_id}")
    public String home(Model model, Integer section_id,
                       @RequestParam("page") Integer page) {
//        model.addAttribute("title", "main page");
//        log.debug("at home via /" + p);
        return "home";
    }
    @GetMapping("/forum/{section_id}/{topic_id}")
    public String home(Model model, Integer section_id,
                       Integer topic_id,
                       @RequestParam("page") Integer page) {
//        model.addAttribute("title", "main page");
//        log.debug("at home via /" + p);
        return "home";
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
    public String usercard(Model model, Integer id) {
        return "usercard";
    }
}
