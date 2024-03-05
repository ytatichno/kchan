package com.maxdev.kchan.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

/**
 * Created by ytati
 * on 04.03.2024.
 * @todo add constraints
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
    private Date created;
    @ManyToOne
    @JoinColumn(name = "author")
    private Usercard author;
    @ManyToOne
    @JoinColumn(name = "status")
    private TopicStatus status;
    @ManyToOne
    @JoinColumn(name = "section")
    private Section section;

}
