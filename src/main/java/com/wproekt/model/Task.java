package com.wproekt.model;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;


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
