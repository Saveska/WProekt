package com.wproekt.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class TaskCard extends Card {
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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
    public void removeTask(Task task) {
        tasks.remove(task);
    }
    public void removeAllTasks(){
        tasks.clear();
    }
    public List<Task> setTasks(List<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);

        return this.tasks;
    }
}
