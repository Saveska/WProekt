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

    public Label() {
    }

    public Label(String name) {
        this.name = name;

        
    }
}
