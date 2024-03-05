package com.maxdev.kchan.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * Created by ytati
 * on 04.03.2024.
 *
 * @todo add constraints
 */
@Entity
@Table(name = "message_statuses")
@Data
public class MessageStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(max = 14)
    private String name;
}
