package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Feed {

    LocalDateTime timestamp;

    Long userId;

    String event;

    String operation;

    Long eventId;

    Long entityId;

    public Feed(LocalDateTime timestamp, Long userId, String event, String operation, Long eventId, Long entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.event = event;
        this.operation = operation;
        this.eventId = eventId;
        this.entityId = entityId;
    }
}
