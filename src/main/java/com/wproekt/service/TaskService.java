package com.wproekt.service;

import com.wproekt.model.Task;
import com.wproekt.model.TaskCard;

import java.util.List;
import java.util.Map;

public interface TaskService {
    List<Task> createList(Map<String, Object> map);

    TaskCard addTasksToTaskCard(TaskCard taskCard, List<Task> tasks);

    void setTaskBoolean(Long id, Boolean isCompleted);
}
