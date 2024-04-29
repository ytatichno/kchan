package com.maxdev.kchan.models;

import com.maxdev.kchan.models.enums.TopicStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by ytati
 * on 04.03.2024.
 */
@Entity
@Table(name = "topics")
@Data
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private String description = "";
    private Date created = Date.valueOf(LocalDate.now());
    @ManyToOne
    @JoinColumn(name = "author")
    private Usercard author;
    @Column(name = "status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TopicStatus status;
    @ManyToOne
    @JoinColumn(name = "section", nullable = false)
    private Section section;

}
