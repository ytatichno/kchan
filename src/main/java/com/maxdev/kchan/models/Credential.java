package com.maxdev.kchan.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

/**
 * Created by ytati
 * on 04.03.2024.
 * @todo add constraints
 */
@Entity
@Table(name="credentials")
@Data
public class Credential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String salt;
    private Integer saltmode;
    private String pwd;
    @OneToOne
    @JoinColumn(name = "usercard")
    private Usercard usercard;
}
