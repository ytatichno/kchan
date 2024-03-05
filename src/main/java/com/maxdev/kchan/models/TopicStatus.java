package com.maxdev.kchan.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by ytati
 * on 04.03.2024.
 *
 * @todo add constraints
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
