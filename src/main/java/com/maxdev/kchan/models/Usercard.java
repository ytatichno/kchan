package com.maxdev.kchan.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by ytati
 * on 04.03.2024.
 *
 * @todo add constraints
 */
@Entity
@Table(name = "usercards")
@Data
public class Usercard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nick;
    private String about;
    @Null
    private Date birthday;
    private Date regdate;
    private Boolean isAdmin;
    private Integer messages;
    @ManyToMany(mappedBy = "moder_id")
    @JoinTable(name = "sections_moders",
            joinColumns = @JoinColumn(name = "moder_id"),
            inverseJoinColumns = @JoinColumn(name = "section_id")
    )
    private ArrayList<Section> moderableSections;
}
