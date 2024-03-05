package com.maxdev.kchan.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

/**
 * Created by ytati
 * on 04.03.2024.
 * @todo add constraints
 */
@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    @ManyToOne
    @JoinColumn(name = "topic")
    private Topic topic;
    @ManyToOne
    @JoinColumn(name = "author")
    private Usercard author;
    @NotNull
    private String message;
    @NotNull
    private Timestamp created;
    @ManyToOne
    @JoinColumn(name="status")
    private MessageStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reply")
    private Message reply;
}
