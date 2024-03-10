package com.maxdev.kchan.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by ytati
 * on 04.03.2024.
 */
@Entity
@Table(name = "sections")
@Data
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private String description = "";
    private Date created = Date.valueOf(LocalDate.now());
    @ManyToMany(mappedBy = "moderableSections")
    private ArrayList<Usercard> moders;

    private void setId(Integer id){
        this.id = id;
    }
}
