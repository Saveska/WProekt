package com.wproekt.service.impl;

import com.wproekt.model.Task;
import com.wproekt.model.TaskCard;
import com.wproekt.repository.CardRepository;
import com.wproekt.repository.TaskRepository;
import com.wproekt.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImplementation implements TaskService {

    TaskRepository taskRepository;
    CardRepository cardRepository;

    public TaskServiceImplementation(TaskRepository taskRepository, CardRepository cardRepository) {
        this.taskRepository = taskRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public List<Task> createList(Map<String, Object> map) {
        List<Task> taskList = new ArrayList<>();
        map.forEach((x, y) -> {
            Task task = new Task(x, !y.equals(Boolean.FALSE));
            taskRepository.save(task);
            taskList.add(task);
        });
        return taskList;
    }

    @Override
    public TaskCard addTasksToTaskCard(TaskCard taskCard, List<Task> tasks) {
        tasks.forEach(taskCard::addTask);
        cardRepository.save(taskCard);
        return taskCard;
    }

    @Override
    public void setTaskBoolean(Long id, Boolean isCompleted) {
        Task task = taskRepository.getReferenceById(id);
        task.setIsCompleted(isCompleted);
        taskRepository.save(task);
    }
    //TODO: proveri dali postoj task so takov id
    @Override
    public Task editTask(Long id, String text) {
        Task task = taskRepository.getReferenceById(id);
        task.setText(text);

        return taskRepository.save(task);

    }
}
