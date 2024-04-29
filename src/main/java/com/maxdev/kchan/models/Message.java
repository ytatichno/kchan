package com.maxdev.kchan.models;

import com.maxdev.kchan.models.enums.MessageStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    @Column(name = "status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private MessageStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply")
    private Message reply;

    public Message(String message) {
        this.message = message;
        status = MessageStatus.CASUAL;
    }

    public Message() {

    }
}


