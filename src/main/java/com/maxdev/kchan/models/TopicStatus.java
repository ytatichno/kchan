package com.maxdev.kchan.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Created by ytati
 * on 04.03.2024.
 */
@Entity
@Table(name = "topic_statuses")
@Data
public class TopicStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(max = 14)
    private String name;
}
