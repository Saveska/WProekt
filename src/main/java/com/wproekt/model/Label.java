package com.wproekt.model;

import lombok.Data;

import jakarta.persistence.*;


@Entity
@Data
public class Label {
    @Id
    @GeneratedValue

    private Long Id;
    private String name;




    public Label() {
    }

    public Label(String name) {
        this.name = name;
    }
}
