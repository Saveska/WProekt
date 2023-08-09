package com.wproekt.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
@Data
public class Task {
    @Id
    @GeneratedValue

    private Long Id;
    private String text;
    private Boolean isCompleted;

    public Task() {
    }

    public Task(String text) {
        this.text = text;
        this.isCompleted = false;
    }

    public Task(String text, Boolean isCompleted) {
        this.text = text;
        this.isCompleted = isCompleted;
    }
}
