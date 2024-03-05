package com.maxdev.kchan.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by ytati
 * on 04.03.2024.
 */
@Entity
@Table(name = "usercards")
@Data
public class Usercard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false, name = "nick")
    private String nick;
    @NotNull
    private String about = "";
    @Null
    private Date birthday;
    @NotNull
    private Date regdate = Date.valueOf(LocalDate.now());
    @NotNull
    private Boolean isAdmin = false;
    @NotNull
    @Min(value = 0)
    private Integer messages = 0;
    @ManyToMany(mappedBy = "moder_id")
    @JoinTable(name = "sections_moders",
            joinColumns = @JoinColumn(name = "moder_id"),
            inverseJoinColumns = @JoinColumn(name = "section_id")
    )
    private ArrayList<Section> moderableSections;
}
