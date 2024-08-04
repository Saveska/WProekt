package com.wproekt.model.eventListeners;

import com.wproekt.model.Card;


import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

public class LastUpdatedListener {

    @PreUpdate
    public void setLastUpdated(Card card) {
        card.setDateLastUpdated(LocalDateTime.now());
    }
}