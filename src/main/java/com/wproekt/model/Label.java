package com.wproekt.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Entity
@Data
public class Label {
    @Id
    @GeneratedValue

    private Long Id;

    private String name;

    @ManyToMany
    private List<Card> cards;
    @ManyToOne
    private User user;
    public Label() {
    }

    public Label(String name, User user) {
        this.name = name;
        this.user = user;
        
    }
}
