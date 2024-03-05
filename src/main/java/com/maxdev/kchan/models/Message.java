package com.maxdev.kchan.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Created by ytati
 * on 04.03.2024.
 */
@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    @ManyToOne
    @JoinColumn(name = "topic", nullable = false)
    private Topic topic;
    @ManyToOne
    @JoinColumn(name = "author", nullable = false)
    private Usercard author;
    @NotNull
    private String message;
    @NotNull
    private Timestamp created = Timestamp.from(Instant.now());
    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private MessageStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply")
    private Message reply;
}
