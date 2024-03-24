package com.maxdev.kchan.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * Created by ytati
 * on 04.03.2024.
 */
@Entity
@Table(name = "usercards")
@Data
@NoArgsConstructor
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
    @ManyToMany
    @JoinTable(name = "sections_moders",
            joinColumns = @JoinColumn(
                    name = "moder_id",
                    nullable = false
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "section_id",
                    nullable = false
            )
    )
    private Set<Section> moderableSections;

    public Usercard(Map<String, Object> tupleToExtractFrom) {
        id = (Integer) tupleToExtractFrom.get("id");
        nick = (String) tupleToExtractFrom.get("nick");
        about = (String) tupleToExtractFrom.get("about");
        birthday = (Date) tupleToExtractFrom.get("birthday");
        regdate = (Date) tupleToExtractFrom.get("regdate");
        isAdmin = (Boolean) tupleToExtractFrom.get("is_admin");
        messages = (Integer) tupleToExtractFrom.get("messages");
    }
}
