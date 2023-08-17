package com.wproekt.service.impl;

import com.wproekt.model.Task;
import com.wproekt.model.TaskCard;
import com.wproekt.repository.CardRepository;
import com.wproekt.repository.TaskCardRepository;
import com.wproekt.repository.TaskRepository;
import com.wproekt.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImplementation implements TaskService {

    TaskRepository taskRepository;
    TaskCardRepository taskCardRepository;
    CardRepository cardRepository;


    public TaskServiceImplementation(TaskRepository taskRepository, TaskCardRepository taskCardRepository, CardRepository cardRepository) {
        this.taskRepository = taskRepository;
        this.taskCardRepository = taskCardRepository;
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

    @Override
    public Task addTask(Long cardId, String text) {
        TaskCard taskCard = taskCardRepository.getReferenceById(cardId);
        Task task = new Task(text);

        taskRepository.save(task);

        taskCard.getTasks().add(task);

        taskCardRepository.save(taskCard);
        return task;
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId, Long cardId) {
        Task task = taskRepository.getReferenceById(taskId);
        TaskCard taskCard = taskCardRepository.getReferenceById(cardId);
        taskCard.getTasks().remove(task);

        taskCardRepository.save(taskCard);

        taskRepository.delete(task);
    }

    @Override
    @Transactional
    public void changeCardOfTask(Long taskId, Long siblingId, Long cardSource, Long cardTarget) {
        Task task = taskRepository.getReferenceById(taskId);

        TaskCard taskCardSource = taskCardRepository.getReferenceById(cardSource);
        TaskCard taskCardTarget = taskCardRepository.getReferenceById(cardTarget);

        if(taskCardSource != taskCardTarget){
            taskCardSource.getTasks().remove(task);
            taskCardTarget.getTasks().add(task);
        }


        //TODO: da se cuva redosled

        taskCardRepository.save(taskCardSource);
        taskCardRepository.save(taskCardTarget);
        taskRepository.save(task);

    }
}
