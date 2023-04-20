package com.wproekt.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class TaskCard extends Card{
    @OneToMany
    private List<Task> tasks;

    public TaskCard() {
    }
}
