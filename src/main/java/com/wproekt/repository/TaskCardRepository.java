package com.wproekt.repository;

import com.wproekt.model.TaskCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TaskCardRepository extends JpaRepository<TaskCard,Long> {
    @Modifying
    @Query(value = "delete from card_tasks where tasks_id = :id",nativeQuery = true)
    void deleteTask(Long id);
}
