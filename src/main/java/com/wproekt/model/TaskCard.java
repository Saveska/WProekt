package com.wproekt.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class TaskCard extends Card {
    @OneToMany(fetch = FetchType.EAGER)
    @OrderBy()
    private List<Task> tasks;

    public TaskCard() {
        super("Task");
        this.tasks = new ArrayList<>();
    }


    public TaskCard(String title) {
        super(title);
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }
}
