package com.wproekt.model.eventListeners;

import com.wproekt.model.Card;
import org.apache.tomcat.jni.Time;

import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.Date;

public class LastUpdatedListener {

    @PreUpdate
    public void setLastUpdated(Card card) {
        card.setDateLastUpdated(LocalDateTime.now());
    }
}