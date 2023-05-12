package com.wproekt.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Note extends Card{

    private String text;

    public Note() {
    }

    public Note(String title, String text) {
        super(title);
        this.text = text;
    }

}
