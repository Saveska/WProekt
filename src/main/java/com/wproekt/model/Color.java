package com.wproekt.model;

import lombok.Data;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Color {

    @Id
    @GeneratedValue
    private Long Id;
    private java.awt.Color color;

    public Color() {
        color = java.awt.Color.darkGray;
    }

    public Color(java.awt.Color color) {
        this.color = color;
    }
}
