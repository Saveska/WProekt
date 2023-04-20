package com.wproekt.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
public class Label {
    @Id
    @GeneratedValue

    private Integer Id;
    @ManyToOne
    private Color color;

    private String name;

    public Label() {
    }
}
