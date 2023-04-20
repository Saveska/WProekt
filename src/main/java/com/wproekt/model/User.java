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

    private String password;
    private String name;
    private String surname;
    @OneToMany
    private List<Card> cards;

    public User() {
    }

}
