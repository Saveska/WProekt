package com.wproekt.model;

import com.wproekt.model.eventListeners.LastUpdatedListener;
import lombok.Data;

import javax.persistence.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Set;

@Entity
@EntityListeners(LastUpdatedListener.class)
@Data
abstract public class Card {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String imageLink;
    private LocalDateTime dateCreated;
    private LocalDateTime dateLastUpdated;
    private Boolean isPinned;
    private Boolean isArchived;
    private Boolean isInBin;
    private Color color;
    private Integer position;

    @ManyToMany
    private Set<Label> label;

    public Card() {
        this.color = new Color(185, 86, 185);
    }

    public Card(String title) {
        this.title = title;
        this.dateCreated = LocalDateTime.now();
        this.dateLastUpdated = LocalDateTime.now();
        //TODO: od ajax update da se menuva
        this.isPinned = false;
        this.isArchived = false;
        this.isInBin = false;
        this.color = new Color(185, 86, 185);
    }

    public String getRGBColor() {
        return "rgb(" + color.getRed() + ',' + color.getGreen() + ',' + color.getBlue() + ')';
    }

    public String getBrighterRGB() {
        Color newColor = color.brighter();
        return "rgb(" + newColor.getRed() + ',' + newColor.getGreen() + ',' + newColor.getBlue() + ')';
    }

    public String getBackgroundColor() {
        return "rgba(" + color.getRed() + ',' + color.getGreen() + ',' + color.getBlue() + ",0.1)";

    }
}
