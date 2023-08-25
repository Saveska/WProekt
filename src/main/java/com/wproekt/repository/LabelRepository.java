package com.wproekt.repository;

import com.wproekt.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LabelRepository extends JpaRepository<Label,Long> {

    @Modifying
    @Query(value = "delete from card_label where label_id = :id",nativeQuery = true)
    void deleteFromCardLabel(Long id);


}
