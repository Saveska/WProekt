package com.wproekt.model;

import lombok.Data;

import javax.persistence.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Card {
    @Id
    @GeneratedValue
    private Long   id;

    private String title;
    //TODO: da se odlucime dali vo static site sliki ce bida ili so blob vo db
    //private String imageLink;
    private LocalDateTime dateCreated;
    private LocalDateTime dateLastUpdated;
    private Boolean isPinned;
    private Boolean isArchived;
    private Boolean isInBin;

    private Color color;
    @ManyToMany
    private List<Label> label;

    public Card() {
    }

    public Card(String title) {
        this.title = title;
        this.dateCreated = LocalDateTime.now();
        this.dateLastUpdated = LocalDateTime.now();
        this.isPinned = false;
        this.isArchived = false;
        this.isInBin = false;
    }
}
