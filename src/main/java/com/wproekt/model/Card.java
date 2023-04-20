package com.wproekt.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Card {
    @Id
    @GeneratedValue
    private Integer id;

    private String title;
    //private String imageLink;
    private LocalDateTime dateCreated;
    private LocalDateTime dateLastUpdated;
    private Boolean isPinned;
    private Boolean isArchived;
    private Boolean isInBin;
    @ManyToOne
    private Color color;
    @ManyToMany
    private List<Label> label;

    public Card() {
    }


}
