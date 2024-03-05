package com.maxdev.kchan.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created by ytati
 * on 04.03.2024.
 */
@Entity
@Table(name = "credentials")
@Data
public class Credential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Email
    @Column(unique = true, name = "email", nullable = false)
    private String email;
    @NotNull
    private String salt;
    @NotNull
    private Integer saltmode = 1;
    @NotNull
    private String pwd;
    @OneToOne
    @JoinColumn(name = "usercard")
    private Usercard usercard;
}
