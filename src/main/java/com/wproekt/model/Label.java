package com.wproekt.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.awt.*;

@Entity
@Data
public class Label {
    @Id
    @GeneratedValue

    private Long Id;
    private Color color;

    private String name;

    public Label() {
    }

    public Label(String name) {
        this.name = name;
        
    }
}
