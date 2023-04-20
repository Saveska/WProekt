package com.wproekt.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity(name = "taskUser")
@Data
public class User {

    @Id
    @GeneratedValue

    private Integer id;

    @Column(unique = true)
    private String username;
    private String password;
    private String name;
    private String surname;
    @OneToMany
    private List<Card> cards;

    public User() {
    }

    public User(String username, String password, String name, String surname) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }
}
